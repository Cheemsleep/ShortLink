package com.cfl.shortlink.admin.remote;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfl.shortlink.admin.common.convention.result.Result;
import com.cfl.shortlink.admin.remote.dto.req.*;
import com.cfl.shortlink.admin.remote.dto.resp.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 短链接中台远程调用服务
 */
@FeignClient("short-link-project")
public interface ShortLinkActualRemoteService {
    /**
     * 创建短链接
     *
     * @param requestParam 创建短链接请求参数
     * @return 短链接创建响应
     */
    @PostMapping("/api/short-link/v1/create")
    Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam);

    /**
     * 批量创建短链接
     * @param requestParam 批量创建短链接请求参数
     * @return 短链接批量创建响应
     */
    @PostMapping("/api/short-link/v1/create/batch")
    Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam);

    /**
     * 修改短链接
     *
     * @param requestParam 修改短链接请求参数
     */
    @PostMapping("/api/short-link/v1/update")
    Result<Void> updateShortLink(@RequestBody ShortLInkUpdateReqDTO requestParam);

    /**
     * 短链接分页远程调用
     * @param requestParam
     * @return 短链接分页信息
     */
    @GetMapping("/api/short-link/v1/page")
    Result<Page<ShortLInkPageRespDTO>> pageShortLink(@SpringQueryMap ShortLinkPageReqDTO requestParam);

    /**
     * 查询分组短链接总量
     *
     * @param requestParam 分组短链接总量请求参数
     * @return 查询分组短链接总量响应
     */
    @GetMapping("/api/short-link/v1/count")
    Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(@RequestParam("requestParam") List<String> requestParam);

    /**
     * 根据 URL 获取标题
     *
     * @param url 目标网站地址
     * @return 网站标题
     */
    @GetMapping("/api/short-link/v1/title")
    Result<String> getTitleByUrl(@RequestParam("url") String url);

    /**
     * 保存回收站
     *
     * @param requestParam 请求参数
     */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam);

    /**
     * 分页查询回收站短链接
     * @param requestParam 分页查询回收站短链接请求参数
     */
    @GetMapping("/api/short-link/v1/recycle-bin/page")
    Result<Page<ShortLInkPageRespDTO>> pageRecycleBinShortLink(@SpringQueryMap ShortLinkRecycleBinPageReqDTO requestParam);

    /**
     * 恢复短链接
     *
     * @param requestParam 短链接恢复请求参数
     */
    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam);

    /**
     * 移除短链接
     *
     * @param requestParam 短链接移除请求参数
     */
    @PostMapping("/api/short-link/v1/recycle-bin/remove")
    Result<Void> removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO requestParam);

    /**
     * 访问单个短链接指定时间内监控数据
     * @param requestParam 访问单个短链接指定时间内监控数据请求参数
     * @return 短链接监控信息
     */
    @GetMapping("/api/short-link/v1/stats")
    Result<ShortLinkStatsRespDTO> oneShortLinkStats(@SpringQueryMap ShortLinkStatsReqDTO requestParam);

    /**
     * 访问分组下所有链接指定时间内监控数据
     * @param requestParam 访问分组下所有链接指定时间内监控数据请求参数
     * @return 短链接监控信息
     */
    @GetMapping("/api/short-link/v1/stats/group")
    Result<ShortLinkStatsRespDTO> groupShortLinkStats(@SpringQueryMap ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     * @param requestParam 访问单个短链接指定时间内访问记录监控数据请求参数
     * @return 短链接监控记录信息
     */
    @GetMapping("/api/short-link/v1/access-record")
    Result<Page<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(@SpringQueryMap ShortLinkStatsAccessRecordReqDTO requestParam);

    /**
     * 访问分组短链接指定时间内访问记录监控数据
     * @param requestParam 访问分组短链接指定时间内访问记录监控数据请求参数
     * @return 分组短链接监控记录信息
     */
    @GetMapping("/api/short-link/v1/access-record/group")
    Result<Page<ShortLinkGroupStatsAccessRecordRespDTO>> shortLinkStatsAccessGroupRecord(@SpringQueryMap ShortLinkGroupStatsAccessRecordReqDTO requestParam);
}
