package com.cfl.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfl.shortlink.admin.common.constant.RedisCacheConstant;
import com.cfl.shortlink.admin.common.convention.exception.ClientException;
import com.cfl.shortlink.admin.common.convention.exception.ServiceException;
import com.cfl.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.cfl.shortlink.admin.dao.entity.UserDO;
import com.cfl.shortlink.admin.dao.mapper.UserMapper;
import com.cfl.shortlink.admin.dto.req.UserLoginReqDTO;
import com.cfl.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.cfl.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.cfl.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.cfl.shortlink.admin.dto.resp.UserRespDTO;
import com.cfl.shortlink.admin.service.GroupService;
import com.cfl.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate; //redis 工具类
    private final GroupService groupService;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        UserRespDTO result = new UserRespDTO();
        if (userDO == null) {
            throw new ServiceException(UserErrorCodeEnum.USER_NULL);
        }
        BeanUtils.copyProperties(userDO, result);
        return result;
    }

    @Override
    public Boolean hasUsername(String username) {
        return !userRegisterCachePenetrationBloomFilter.contains(username); //应用布隆过滤器
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        if (!hasUsername(requestParam.getUsername())) {
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(RedisCacheConstant.LOCK_USER_REGISTER_KEY + requestParam.getUsername());
        try {
            if (lock.tryLock()) {
                try {
                    int inserted = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
                    if (inserted < 1) {
                        throw new ClientException(UserErrorCodeEnum.USER_SAVE_ERROR);
                    }
                } catch (DuplicateKeyException ex) {
                    throw new ClientException(UserErrorCodeEnum.USER_EXIST);
                }
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
                groupService.saveGroup(requestParam.getUsername() ,"默认分组");
                return;
            }
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {
        // TODO 验证当前用户名是否为登录用户
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                        .eq(UserDO::getUsername, requestParam.getUsername());
        baseMapper.update(BeanUtil.toBean(requestParam, UserDO.class), updateWrapper);
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {

        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                        .eq(UserDO::getUsername, requestParam.getUsername())
                        .eq(UserDO::getPassword, requestParam.getPassword())
                        .eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(queryWrapper);

        if (userDO == null)
            throw new ClientException("用户不存在");

        Boolean hasLogin = stringRedisTemplate.hasKey(RedisCacheConstant.USER_LOGIN_KEY + requestParam.getUsername());

        Map<Object ,Object> hasLoginMap = stringRedisTemplate.opsForHash().entries(RedisCacheConstant.USER_LOGIN_KEY + requestParam.getUsername());
        if (CollUtil.isNotEmpty(hasLoginMap)) {
            String token = hasLoginMap.keySet().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElseThrow(() -> new ClientException("用户登录错误"));
            return new UserLoginRespDTO(token);
        }

        /**
         * 使用三层hash结构
         * Key：login_用户名
         * VALUE:
         *      key:token标识
         *      value: JSON字符串（用户信息）
         */
        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put(RedisCacheConstant.USER_LOGIN_KEY + requestParam.getUsername(),
                uuid, JSON.toJSONString(userDO));
        //48h 有效期
        stringRedisTemplate.expire(RedisCacheConstant.USER_LOGIN_KEY + requestParam.getUsername(), 48L, TimeUnit.HOURS);
        return new UserLoginRespDTO(uuid);
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        return  stringRedisTemplate.opsForHash().get(RedisCacheConstant.USER_LOGIN_KEY + username, token) != null;
    }

    @Override
    public void logout(String username, String token) {
        if (stringRedisTemplate.opsForHash().get(RedisCacheConstant.USER_LOGIN_KEY + username, token) != null) {
            stringRedisTemplate.delete(RedisCacheConstant.USER_LOGIN_KEY + username);
            return;
        }
        throw new ClientException("用户token不存在或未登录");
    }
}
