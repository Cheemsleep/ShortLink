package com.cfl.shortlink.project.mq.consumer;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cfl.shortlink.project.common.constant.RedisKeyConstant;
import com.cfl.shortlink.project.common.constant.ShortLinkConstant;
import com.cfl.shortlink.project.dao.entity.*;
import com.cfl.shortlink.project.dao.mapper.*;
import com.cfl.shortlink.project.dto.bz.ShortLinkStatsRecordDTO;
import com.cfl.shortlink.project.mq.idempotent.MessageQueueIdempotentHandler;
import com.cfl.shortlink.project.mq.producer.LinkStatsSaveRocketMQProducer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.rmi.server.ServerCloneException;
import java.util.*;


/**
 * 短链接监控状态保存消息队列消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LinkStatsSaveRocketMQConsumer implements InitializingBean {

    private final ShortLinkMapper shortLinkMapper;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final RedissonClient redissonClient;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocateStatsMapper linkLocateStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final LinkStatsTodayMapper linkStatsTodayMapper;
    private final LinkStatsSaveRocketMQProducer linkStatsSaveRocketMQProducer;
    private final MessageQueueIdempotentHandler messageQueueIdempotentHandler;

    @Value("${short-link.status.locate.amap-key}")
    private String statsLocateAmapKey;
    @Value("${rocketmq.name-server}")
    private String nameServer;
    @Value("${rocketmq.consumer.group}")
    private String consumerGroup;
    @Value("${rocketmq.producer.topic}")
    private String TOPIC;


    public void onMessage() {
        //1.创建消费者Consumer，制定消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
        //2.指定Nameserver地址
        consumer.setNamesrvAddr(nameServer);
        try {
            //3.订阅主题Topic和Tag
            consumer.subscribe(TOPIC, "*");
            //consumer.subscribe("base", "Tag1");

            //消费所有"*",消费Tag1和Tag2  Tag1 || Tag2
            //consumer.subscribe("base", "*");

            //设定消费模式：负载均衡|广播模式  默认为负载均衡
            //负载均衡10条消息，每个消费者共计消费10条
            //广播模式10条消息，每个消费者都消费10条
            //consumer.setMessageModel(MessageModel.BROADCASTING);

            //4.设置回调函数，处理消息
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                //接受消息内容
                @SneakyThrows
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    for (MessageExt msg : msgs) {
                        String key = msg.getKeys();
                        //判断是否被消费过
                        if (!messageQueueIdempotentHandler.isMessageProcessed(key)) {
                            // 判断当前的这个消息流程是否执行完成
                            if (messageQueueIdempotentHandler.isAccomplish(key)) {
                                throw new ServerCloneException("消息已经完成流程，不能重复消费");
                            }
                            // 消息未完成流程，需要消息队列重试
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                        try {
                            Map<String, String> producerMap = JSON.parseObject(msg.getBody(), Map.class);
                            String fullShortUrl = producerMap.get("fullShortUrl");
                            if (StrUtil.isNotBlank(fullShortUrl)) {
                                String gid = producerMap.get("gid");
                                ShortLinkStatsRecordDTO statsRecord = JSON.parseObject(producerMap.get("statsRecord"), ShortLinkStatsRecordDTO.class);
                                log.info("接收到消息 {}, {}, {}", fullShortUrl, gid, statsRecord.toString());
                                actualSaveShortLinkStats(fullShortUrl, gid, statsRecord);
                            }
                        } catch (Throwable ex) {
                            // 某某某情况宕机了
                            messageQueueIdempotentHandler.delMessageProcessed(key);
                            log.error("记录短链接监控消费异常", ex);
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                        messageQueueIdempotentHandler.setAccomplish(key);
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            //5.启动消费者consumer
            consumer.start();
        } catch (Throwable ex) {
            log.error("消息消费失败");
        }
    }

    private void actualSaveShortLinkStats(String fullShortUrl, String gid, ShortLinkStatsRecordDTO statsRecord) {
        fullShortUrl = Optional.ofNullable(fullShortUrl).orElse(statsRecord.getFullShortUrl());
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(RedisKeyConstant.LOCK_GID_UPDATE_KEY, fullShortUrl));
        RLock rLock = readWriteLock.readLock();
        if (!rLock.tryLock()) {
            Map<String, String> producerMap =new HashMap<>();
            producerMap.put("fullShortUrl", fullShortUrl);
            producerMap.put("gid", gid);
            producerMap.put("statsRecord", JSON.toJSONString(statsRecord));
            linkStatsSaveRocketMQProducer.send(producerMap);
            return;
        }
        try {
            if (StrUtil.isBlank(gid)) {
                LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                        .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
                ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(queryWrapper);
                gid = shortLinkGotoDO.getGid();
            }
            Date now = new Date();
            Week week = DateUtil.dayOfWeekEnum(now);
            int weekValue = week.getIso8601Value();
            int hour = DateUtil.hour(now, true);
            LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                    .pv(1)
                    .uv(statsRecord.getUvFirstFlag() ? 1 : 0)
                    .uip(statsRecord.getUipFirstFlag() ? 1 : 0)
                    .hour(hour)
                    .weekday(weekValue)
                    .fullShortUrl(fullShortUrl)
                    .date(now)
                    .gid(gid)
                    .build();
            linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);
            Map<String, Object> locateParamMap = new HashMap<>();
            locateParamMap.put("key", statsLocateAmapKey);
            locateParamMap.put("ip", statsRecord.getRemoteAddr());
            String locateResultStr = HttpUtil.get(ShortLinkConstant.AMAP_REMOTE_URL, locateParamMap);
            JSONObject locateResultObj = JSON.parseObject(locateResultStr);
            String infoCode = locateResultObj.getString("infocode");
            String actualProvince = "未知";
            String actualCity = "未知";
            LinkLocateStatsDO linkLocateStatsDO;
            if (StrUtil.isNotBlank(infoCode) && StrUtil.equals(infoCode, "10000")) {
                String province = locateResultObj.getString("province");
                boolean unKnownFlag = StrUtil.equals(province, "[]");
                linkLocateStatsDO = LinkLocateStatsDO.builder()
                        .fullShortUrl(fullShortUrl)
                        .gid(gid)
                        .date(now)
                        .cnt(1)
                        .province(actualProvince = unKnownFlag ? "未知" : province)
                        .city(actualCity = unKnownFlag ? "未知" : locateResultObj.getString("city"))
                        .adcode(unKnownFlag ? "未知" : locateResultObj.getString("adcode"))
                        .country("中国")
                        .build();
                linkLocateStatsMapper.shortLinkLocateState(linkLocateStatsDO);
                LinkOsStatsDO linkOsStatsDO = LinkOsStatsDO.builder()
                        .fullShortUrl(fullShortUrl)
                        .gid(gid)
                        .date(now)
                        .cnt(1)
                        .os(statsRecord.getOs())
                        .build();
                linkOsStatsMapper.shortLinkOsState(linkOsStatsDO);
                LinkBrowserStatsDO linkBrowserStatsDO = LinkBrowserStatsDO.builder()
                        .fullShortUrl(fullShortUrl)
                        .gid(gid)
                        .date(now)
                        .cnt(1)
                        .browser(statsRecord.getBrowser())
                        .build();
                linkBrowserStatsMapper.shortLinkBrowserState(linkBrowserStatsDO);
                LinkDeviceStatsDO linkDeviceStatsDO = LinkDeviceStatsDO.builder()
                        .fullShortUrl(fullShortUrl)
                        .gid(gid)
                        .date(now)
                        .cnt(1)
                        .device(statsRecord.getDevice())
                        .build();
                linkDeviceStatsMapper.shortLinkDeviceState(linkDeviceStatsDO);
                LinkNetworkStatsDO linkNetworkStatsDO = LinkNetworkStatsDO.builder()
                        .fullShortUrl(fullShortUrl)
                        .gid(gid)
                        .date(now)
                        .cnt(1)
                        .network(statsRecord.getNetwork())
                        .build();
                linkNetworkStatsMapper.shortLinkNetworkState(linkNetworkStatsDO);
                shortLinkMapper.incrementStats(gid, fullShortUrl, 1, statsRecord.getUvFirstFlag() ? 1 : 0, statsRecord.getUipFirstFlag() ? 1 : 0);
                LinkStatsTodayDO linkStatsTodayDO = LinkStatsTodayDO.builder()
                        .fullShortUrl(fullShortUrl)
                        .gid(gid)
                        .date(now)
                        .todayPv(1)
                        .todayUv(statsRecord.getUvFirstFlag() ? 1 : 0)
                        .todayUip(statsRecord.getUipFirstFlag() ? 1 : 0)
                        .build();
                linkStatsTodayMapper.shortLinkTodayState(linkStatsTodayDO);
                LinkAccessLogsDO linkAccessLogsDO = LinkAccessLogsDO.builder()
                        .fullShortUrl(fullShortUrl)
                        .gid(gid)
                        .ip(statsRecord.getRemoteAddr())
                        .os(statsRecord.getOs())
                        .browser(statsRecord.getBrowser())
                        .network(statsRecord.getNetwork())
                        .device(statsRecord.getDevice())
                        .locate(StrUtil.join("-", "中国", actualProvince, actualCity))
                        .user(statsRecord.getUv())
                        .build();
                linkAccessLogsMapper.insert(linkAccessLogsDO);
            }

        } catch (Throwable ex) {
            log.error("短链接访问量统计异常", ex);
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        onMessage();
    }
}
