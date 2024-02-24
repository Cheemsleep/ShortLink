package com.cfl.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfl.shortlink.project.dao.entity.ShortLinkDO;
import com.cfl.shortlink.project.dto.req.ShortLInkCreateReqDTO;
import com.cfl.shortlink.project.dto.req.ShortLInkUpdateReqDTO;
import com.cfl.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.cfl.shortlink.project.dto.resp.ShortLInkCreateRespDTO;
import com.cfl.shortlink.project.dto.resp.ShortLInkPageRespDTO;
import com.cfl.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * 短链接接口层
 */
public interface ShortLinkService extends IService<ShortLinkDO> {

    /**
     * 创建短链接
     * @param requestParam
     * @return 短链接创建信息
     */
    ShortLInkCreateRespDTO createShortLink(ShortLInkCreateReqDTO requestParam);

    /**
     * 分页查询短链接
     * @param requestParam
     * @return 短链接分页返回结果
     */
    IPage<ShortLInkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    /**
     * 查询短链接分组内数量
     * @param requestParam
     * @return 对应gid下的分组数量
     */
    List<ShortLinkGroupCountQueryRespDTO> ListGroupShortLinkCount(List<String> requestParam);

    /**
     * 修改短链接
     * @param requestParam
     */
    void updateShortLink(ShortLInkUpdateReqDTO requestParam);

    /**
     * 跳转短链接
     * @param shortUri 短链接后缀
     * @param request HTTP 请求
     * @param response HTTP 响应
     */
    void restoreUrl(String shortUri, ServletRequest request, ServletResponse response);
}
