package com.cfl.shortlink.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.cfl.shortlink.admin.dao.entity.UserDO;
import com.cfl.shortlink.admin.dto.req.UserLoginReqDTO;
import com.cfl.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.cfl.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.cfl.shortlink.admin.dto.resp.UserLoginRespDTO;
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

    /**
     * 根据用户名修改用户
     * @param requestParam
     */
    void update(UserUpdateReqDTO requestParam);

    /**
     * 根据用户名与密码登录
     * @param requestParam
     * @return 登录token
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    /**
     * 检查用户是否登录
     * @param username 用户名
     * @param token 用户token
     * @return ture 登录 否则未登录
     */
    Boolean checkLogin(String username, String token);

    /**
     * 用户推出登录
     * @param username 用户名
     * @param token 用户token
     */
    void logout(String username, String token);
}
