package com.cfl.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfl.shortlink.project.dao.entity.ShortLinkDO;
import com.cfl.shortlink.project.dto.req.*;
import com.cfl.shortlink.project.dto.resp.ShortLinkPageRespDTO;

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
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam);

    /**
     * 从回收站恢复短链接
     * @param requestParam
     */
    void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam);

    /**
     * 从回收站删除短链接
     * @param requestParam
     */
    void removeRecycleBin(RecycleBinRemoveReqDTO requestParam);
}
