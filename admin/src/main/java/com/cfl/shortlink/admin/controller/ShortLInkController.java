package com.cfl.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfl.shortlink.admin.common.convention.result.Result;
import com.cfl.shortlink.admin.common.convention.result.Results;
import com.cfl.shortlink.admin.remote.ShortLinkRemoteService;
import com.cfl.shortlink.admin.remote.dto.req.ShortLinkBatchCreateReqDTO;
import com.cfl.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.cfl.shortlink.admin.remote.dto.req.ShortLInkUpdateReqDTO;
import com.cfl.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.cfl.shortlink.admin.remote.dto.resp.ShortLInkPageRespDTO;
import com.cfl.shortlink.admin.remote.dto.resp.ShortLinkBaseInfoRespDTO;
import com.cfl.shortlink.admin.remote.dto.resp.ShortLinkBatchCreateRespDTO;
import com.cfl.shortlink.admin.util.EasyExcelWebUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后管系统远程调用controller层
 */
@RestController
public class ShortLInkController {

    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/admin/v1/create")
    public Result createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return shortLinkRemoteService.createShortLink(requestParam);
    }

    /**
     * 批量创建短链接
     */
    @PostMapping("/api/short-link/admin/v1/create/batch")
    public Result<Void> batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam, HttpServletResponse response) {
        Result<ShortLinkBatchCreateRespDTO> shortLinkBatchCreateRespDTOResult = shortLinkRemoteService.batchCreateShortLink(requestParam);
        if (shortLinkBatchCreateRespDTOResult.isSuccess()) {
            List<ShortLinkBaseInfoRespDTO> baseLinkInfos = shortLinkBatchCreateRespDTOResult.getData().getBaseLinkInfos();
            EasyExcelWebUtil.write(response, "批量创建短链接-SaaS短链接系统", ShortLinkBaseInfoRespDTO.class, baseLinkInfos);
        }
        return Results.success();
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLInkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return shortLinkRemoteService.pageShortLink(requestParam);
    }

    /**
     * 短链接修改功能
     */
    @PostMapping("/api/short-link/admin/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLInkUpdateReqDTO requestParam) {
        return shortLinkRemoteService.updateShortLink(requestParam);
    }

}
