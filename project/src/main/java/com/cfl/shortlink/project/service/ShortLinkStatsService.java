package com.cfl.shortlink.project.service;

import com.cfl.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.cfl.shortlink.project.dto.resp.ShortLinkStatsRespDTO;


/**
 * 短链接监控服务接口
 */
public interface ShortLinkStatsService {
    /**
     * 获取单个短链接监控数据
     * @param requestParam 获取短链接监控数据入参
     * @return 短链接监控数据
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam);
}
