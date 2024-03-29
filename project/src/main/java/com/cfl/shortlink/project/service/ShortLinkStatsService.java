package com.cfl.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfl.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.cfl.shortlink.project.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import com.cfl.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.cfl.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.cfl.shortlink.project.dto.resp.ShortLinkGroupStatsAccessRecordRespDTO;
import com.cfl.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
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

    /**
     * 获取分组短链接监控数据
     *
     * @param requestParam 获取分组短链接监控数据入参
     * @return 分组短链接监控数据
     */
    ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam);

    /**
     *  访问单个短链接指定时间内访问记录监控数据
     * @param requestParam
     * @return 短链接监控记录
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam);

    /**
     *  访问分组短链接指定时间内访问记录监控数据
     * @param requestParam
     * @return 分组短链接监控记录
     */
    IPage<ShortLinkGroupStatsAccessRecordRespDTO> shortLinkStatsAccessGroupRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam);
}
