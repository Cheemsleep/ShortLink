package com.cfl.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfl.shortlink.admin.common.convention.result.Result;
import com.cfl.shortlink.admin.common.convention.result.Results;
import com.cfl.shortlink.admin.remote.ShortLinkRemoteService;
import com.cfl.shortlink.admin.remote.dto.req.ShortLInkCreateReqDTO;
import com.cfl.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.cfl.shortlink.admin.remote.dto.resp.ShortLInkPageRespDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public Result createShortLink(@RequestBody ShortLInkCreateReqDTO requestParam) {
        return shortLinkRemoteService.createShortLink(requestParam);
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLInkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return shortLinkRemoteService.pageShortLink(requestParam);
    }

}