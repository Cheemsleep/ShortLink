package com.cfl.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfl.shortlink.project.dao.entity.ShortLinkDO;
import com.cfl.shortlink.project.dto.req.ShortLInkCreateReqDTO;
import com.cfl.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.cfl.shortlink.project.dto.resp.ShortLInkCreateRespDTO;
import com.cfl.shortlink.project.dto.resp.ShortLInkPageRespDTO;
import com.cfl.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;

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
}
