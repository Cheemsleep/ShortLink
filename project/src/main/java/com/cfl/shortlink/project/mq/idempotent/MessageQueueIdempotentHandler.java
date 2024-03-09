package com.cfl.shortlink.project.mq.idempotent;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cfl.shortlink.project.dao.entity.LinkAccessLogsDO;
import com.cfl.shortlink.project.dao.mapper.LinkAccessLogsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 消息队列幂等处理器 Redis层
 */
@Component
@RequiredArgsConstructor
public class MessageQueueIdempotentHandler {

    private final StringRedisTemplate stringRedisTemplate;
    private final LinkAccessLogsMapper linkAccessLogsMapper;

    /**
     * 判断当前消息是否消费过
     *
     * @param messageKey 消息唯一标识
     * @return 消息是否消费过
     */
    public boolean isMessageProcessed(String messageKey) {
        //如果为空就set值，并返回1
        //如果存在(不为空)不进行操作，并返回0
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(messageKey, "0", 2, TimeUnit.MINUTES));
    }

    /**
     * 判断消息消费流程是否执行完成
     *
     * @param messageKey 消息唯一标识
     * @return 消息是否执行完成
     */
    public boolean isAccomplish(String messageKey) {
        return Objects.equals(stringRedisTemplate.opsForValue().get(messageKey), "1");
    }

    /**
     * 设置消息流程执行完成
     *
     * @param messageKey 消息唯一标识
     */
    public void setAccomplish(String messageKey) {
        stringRedisTemplate.opsForValue().set(messageKey, "1", 2, TimeUnit.MINUTES);
    }

    /**
     * 如果消息处理遇到异常情况，删除幂等标识
     *
     * @param messageKey 消息唯一标识
     */
    public void delMessageProcessed(String messageKey) {
        stringRedisTemplate.delete(messageKey);
    }

}
