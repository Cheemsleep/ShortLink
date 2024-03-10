package com.cfl.shortlink.project.mq.consumer;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.cfl.shortlink.project.dto.bz.ShortLinkStatsRecordDTO;
import com.cfl.shortlink.project.mq.idempotent.MessageQueueIdempotentHandler;
import com.cfl.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.rmi.server.ServerCloneException;
import java.util.*;


/**
 * 短链接监控状态保存消息队列延迟消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LinkStatsDelaySaveRocketMQConsumer implements InitializingBean {

    private final ShortLinkService shortLinkService;
    private final MessageQueueIdempotentHandler messageQueueIdempotentHandler;

    @Value("${rocketmq.name-server}")
    private String nameServer;
    @Value("${rocketmq.consumer.group}")
    private String consumerGroup;
    @Value("${rocketmq.producer.topic}")
    private String TOPIC;
    @Value("${rocketmq.producer.consume_delay}")
    private String TAG_DELAY;


    public void onMessage() {
        //1.创建消费者Consumer，制定消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
        //2.指定Nameserver地址
        consumer.setNamesrvAddr(nameServer);
        try {
            //3.订阅主题Topic和Tag
            consumer.subscribe(TOPIC, TAG_DELAY);
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
                                //重新执行统计方法
                                shortLinkService.shortLinkStats(fullShortUrl, gid, statsRecord);
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



    @Override
    public void afterPropertiesSet() throws Exception {
        onMessage();
    }
}
