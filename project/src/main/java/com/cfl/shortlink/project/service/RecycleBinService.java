package com.cfl.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfl.shortlink.project.dao.entity.ShortLinkDO;
import com.cfl.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.cfl.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.cfl.shortlink.project.dto.resp.ShortLInkPageRespDTO;

/**
 * 回收站服务接口层
 */
public interface RecycleBinService extends IService<ShortLinkDO> {
    /**
     * 保存至回收站
     * @param requestParam
     */
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);

    /**
     * 分页查询回收站短链接
     * @param requestParam
     * @return 短链接分页返回结果
     */
    IPage<ShortLInkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);
}
