package com.cfl.shortlink.admin.common.biz.user;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.cfl.shortlink.admin.common.convention.exception.ClientException;
import com.cfl.shortlink.admin.common.convention.result.Results;
import com.cfl.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.google.common.collect.Lists;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

/**
 * 用户信息传输过滤器
 */
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    private static final List<String> IGNORE_URI = Lists.newArrayList(
            "/api/short-link/admin/v1/user/login",
            "/api/short-link/admin/v1/user/has-username",
            "/api/short-link/admin/v1/user"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();
        if (!IGNORE_URI.contains(requestURI)) {
            String username = httpServletRequest.getHeader("username");
            String token = httpServletRequest.getHeader("token");
            if (!StrUtil.isAllBlank(username, token)) {
                returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL))));
                return;
            }
            Object userInfoJsonStr = null;
            try {
                userInfoJsonStr = stringRedisTemplate.opsForHash().get("login_" + username, token);

                if (userInfoJsonStr == null) {
                    throw new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL);
                }

            } catch (Exception e) {
                returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL))));
                return;
            }
            if (userInfoJsonStr != null) {
                UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(), UserInfoDTO.class);
                UserContext.setUser(userInfoDTO);
            }
        }

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }

    private void returnJson(HttpServletResponse response, String json) {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);
        } catch (IOException e) {

        } finally {
            if (writer != null)
                writer.close();
        }
    }
}