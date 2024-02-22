package com.cfl.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cfl.shortlink.project.dao.entity.ShortLinkDO;
import com.cfl.shortlink.project.dto.req.ShortLInkCreateReqDTO;
import com.cfl.shortlink.project.dto.resp.ShortLInkCreateRespDTO;

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
}
