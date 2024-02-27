package com.cfl.shortlink.project.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfl.shortlink.project.common.convention.result.Result;
import com.cfl.shortlink.project.common.convention.result.Results;
import com.cfl.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import com.cfl.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.cfl.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.cfl.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.cfl.shortlink.project.dto.resp.ShortLInkPageRespDTO;
import com.cfl.shortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回收站控制层
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {
    private final RecycleBinService recycleBinService;

    /**
     * 保存至回收站
     */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void> save(@RequestBody RecycleBinSaveReqDTO requestParam) {
        recycleBinService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 分页查询回收站短链接
     */
    @GetMapping("/api/short-link/v1/recycle-bin/page")
    public Result<IPage<ShortLInkPageRespDTO>> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        return Results.success(recycleBinService.pageShortLink(requestParam));
    }

    /**
     * 恢复短链接
     */
    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam) {
        recycleBinService.recoverRecycleBin(requestParam);
        return Results.success();
    }
}
