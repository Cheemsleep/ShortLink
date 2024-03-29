package com.cfl.shortlink.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfl.shortlink.admin.common.convention.result.Result;
import com.cfl.shortlink.admin.remote.dto.req.RecycleBinRecoverReqDTO;
import com.cfl.shortlink.admin.remote.dto.req.RecycleBinRemoveReqDTO;
import com.cfl.shortlink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import com.cfl.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.cfl.shortlink.admin.remote.dto.resp.ShortLInkPageRespDTO;

/**
 * URL 接口层
 */
public interface RecycleBinService {

    /**
     * 保存至回收站
     * @param requestParam
     */
    Result<Void> saveRecycleBin(RecycleBinSaveReqDTO requestParam);


    /**
     * 分页查询回收站短链接
     * @param requestParam
     */
    Result<Page<ShortLInkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam);

    /**
     * 从回收站恢复短链接
     * @param requestParam
     */
    Result<Void> recoverRecycleBin(RecycleBinRecoverReqDTO requestParam);

    /**
     * 从回收站删除短链接
     * @param requestParam
     * @return
     */
    Result<Void> removeRecycleBin(RecycleBinRemoveReqDTO requestParam);
}
