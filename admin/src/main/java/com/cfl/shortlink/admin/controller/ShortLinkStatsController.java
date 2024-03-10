package com.cfl.shortlink.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfl.shortlink.admin.common.convention.result.Result;
import com.cfl.shortlink.admin.remote.ShortLinkActualRemoteService;
import com.cfl.shortlink.admin.remote.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import com.cfl.shortlink.admin.remote.dto.req.ShortLinkGroupStatsReqDTO;
import com.cfl.shortlink.admin.remote.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.cfl.shortlink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import com.cfl.shortlink.admin.remote.dto.resp.ShortLinkGroupStatsAccessRecordRespDTO;
import com.cfl.shortlink.admin.remote.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.cfl.shortlink.admin.remote.dto.resp.ShortLinkStatsRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ShortLinkStatsController {

    private final ShortLinkActualRemoteService actualRemoteService;

    /**
     * 访问单个短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return actualRemoteService.oneShortLinkStats(requestParam);
    }

    /**
     * 访问分组短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats/group")
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam) {
        return actualRemoteService.groupShortLinkStats(requestParam);
    }

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/short-link/admin/v1/access-record")
    public Result<Page<ShortLinkStatsAccessRecordRespDTO>> shortLinkStats(ShortLinkStatsAccessRecordReqDTO requestParam) {
        return actualRemoteService.shortLinkStatsAccessRecord(requestParam);
    }

    /**
     * 访问分组下短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/short-link/admin/v1/access-record/group")
    public Result<Page<ShortLinkGroupStatsAccessRecordRespDTO>> groupStats(ShortLinkGroupStatsAccessRecordReqDTO requestParam) {
        return actualRemoteService.shortLinkStatsAccessGroupRecord(requestParam);
    }
}
