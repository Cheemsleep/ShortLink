package com.cfl.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cfl.shortlink.project.dao.entity.ShortLinkDO;
import com.cfl.shortlink.project.dto.req.RecycleBinSaveReqDTO;

/**
 * 回收站服务接口层
 */
public interface RecycleBinService extends IService<ShortLinkDO> {
    /**
     * 保存至回收站
     * @param requestParam
     */
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);
}
