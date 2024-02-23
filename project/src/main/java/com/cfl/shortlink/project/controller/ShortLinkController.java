package com.cfl.shortlink.project.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfl.shortlink.project.common.convention.result.Result;
import com.cfl.shortlink.project.common.convention.result.Results;
import com.cfl.shortlink.project.dto.req.ShortLInkCreateReqDTO;
import com.cfl.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.cfl.shortlink.project.dto.resp.ShortLInkPageRespDTO;
import com.cfl.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
