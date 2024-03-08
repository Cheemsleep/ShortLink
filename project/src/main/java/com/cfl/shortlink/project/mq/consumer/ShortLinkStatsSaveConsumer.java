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
import com.cfl.shortlink.project.mq.producer.DelayShortLinkStatsProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 短链接监控状态保存消息队列消费者
 * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：link）获取项目资料
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShortLinkStatsSaveConsumer implements StreamListener<String, MapRecord<String, String, String>> {

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
    private final DelayShortLinkStatsProducer delayShortLinkStatsProducer;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${short-link.status.locate.amap-key}")
    private String statusLocateAmapKey;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        String stream = message.getStream();
        RecordId id = message.getId();
        Map<String, String> producerMap = message.getValue();
        String fullShortUrl = producerMap.get("fullShortUrl");
        if (StrUtil.isNotBlank(fullShortUrl)) {
            String gid = producerMap.get("gid");
            ShortLinkStatsRecordDTO statsRecord = JSON.parseObject(producerMap.get("statsRecord"), ShortLinkStatsRecordDTO.class);
            actualSaveShortLinkStats(fullShortUrl, gid, statsRecord);
        }
        stringRedisTemplate.opsForStream().delete(Objects.requireNonNull(stream), id.getValue());
    }

    public void actualSaveShortLinkStats(String fullShortUrl, String gid, ShortLinkStatsRecordDTO statsRecord) {
        fullShortUrl = Optional.ofNullable(fullShortUrl).orElse(statsRecord.getFullShortUrl());
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(RedisKeyConstant.LOCK_GID_UPDATE_KEY, fullShortUrl));
        RLock rLock = readWriteLock.readLock();
        if (!rLock.tryLock()) {
            delayShortLinkStatsProducer.send(statsRecord);
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
            locateParamMap.put("key", statusLocateAmapKey);
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
            }

        } catch (Throwable ex) {
            log.error("短链接访问量统计异常", ex);
        } finally {
            rLock.unlock();
        }
    }
}
