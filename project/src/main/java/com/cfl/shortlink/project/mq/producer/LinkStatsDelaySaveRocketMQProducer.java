package com.cfl.shortlink.project.mq.producer;


import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 短链接监控状态保存消息队列延迟生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LinkStatsDelaySaveRocketMQProducer {

    @Value("${rocketmq.name-server}")
    private String nameServer;
    @Value("${rocketmq.producer.group}")
    private String producerGroup;
    @Value("${rocketmq.producer.topic}")
    private String TOPIC;
    @Value("${rocketmq.producer.idempotent_key}")
    private String IDEMPOTENT_KEY;
    @Value("${rocketmq.producer.consume_delay}")
    private String TAG;

    public void send(Map<String, String> producerMap) {
        //1.创建消息生产者producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        //2.指定Nameserver地址
        producer.setNamesrvAddr(nameServer);
        //3.启动producer
        try {
            producer.start();
            //4.创建消息对象，指定主题Topic、Tag和消息体
            /**
             * 参数一：消息主题Topic
             * 参数二：消息Tag 立即消费类型
             * 参数三：消息内容
             */
            Message msg = new Message(TOPIC, TAG, JSON.toJSONBytes(producerMap));
            //设置Key进行消息幂等
            msg.setKeys(IDEMPOTENT_KEY + producerMap.get("fullShortUrl"));
            //5.发送异步消息
            producer.send(msg, new SendCallback() {
                /**
                 * 发送成功回调函数
                 * @param sendResult
                 */
                public void onSuccess(SendResult sendResult) {
                    log.info("发送结果: {}", sendResult);
                }

                /**
                 * 发送失败回调函数
                 * @param e
                 */
                public void onException(Throwable e) {
                    log.error("发送异常：" + e);
                }
            });
            //线程睡1秒
            TimeUnit.SECONDS.sleep(1);
        } catch (Throwable ex) {

        } finally {
            //6.关闭生产者producer
            producer.shutdown();
        }
    }


}
