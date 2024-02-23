package com.cfl.shortlink.project.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfl.shortlink.project.common.convention.result.Result;
import com.cfl.shortlink.project.common.convention.result.Results;
import com.cfl.shortlink.project.dto.req.ShortLInkCreateReqDTO;
import com.cfl.shortlink.project.dto.req.ShortLInkUpdateReqDTO;
import com.cfl.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.cfl.shortlink.project.dto.resp.ShortLInkPageRespDTO;
import com.cfl.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.cfl.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 短链接控制层
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {
    private final ShortLinkService shortLinkService;

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/v1/create")
    public Result createShortLink(@RequestBody ShortLInkCreateReqDTO requestParam) {
        return Results.success(shortLinkService.createShortLink(requestParam));
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLInkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return Results.success(shortLinkService.pageShortLink(requestParam));
    }

    /**
     * 短链接修改功能
     */
    @PutMapping("/api/short-link/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLInkUpdateReqDTO requestParam) {
        shortLinkService.updateShortLink(requestParam);
        return Results.success();
    }

    /**
     * 查询短链接分组下的短链接数量
     */
    @GetMapping("/api/short-link/v1/count")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> ListGroupShortLinkCount(@RequestParam List<String> requestParam) {
        return Results.success(shortLinkService.ListGroupShortLinkCount(requestParam));
    }
}
