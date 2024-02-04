package com.cfl.shortlink.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.cfl.shortlink.admin.dao.entity.UserDO;
import com.cfl.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.cfl.shortlink.admin.dto.resp.UserRespDTO;

/**
 * 用户接口层
 */
public interface UserService extends IService<UserDO> {
    /**
     * 根据用户名查询用户信息
     * @param username
     * @return 用户返回实体
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 查询用户名是否已经存在
     * @param username
     * @return 存在返回 false 不存在返回 true
     */
    Boolean hasUsername(String username);

    /**
     * 注册用户
     * @param requestParam
     */
    void register(UserRegisterReqDTO requestParam);
}
