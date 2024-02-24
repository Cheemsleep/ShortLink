package com.cfl.shortlink.admin.controller;


import com.cfl.shortlink.admin.common.convention.result.Result;
import com.cfl.shortlink.admin.common.convention.result.Results;
import com.cfl.shortlink.admin.remote.ShortLinkRemoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * url标题控制层
 */
@RestController
@RequiredArgsConstructor
public class UrlTitleController {

    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    /**
     * 根据对应url获取对应网站标题
     * @param url
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/title")
    public Result<String> getTitleByUrl(@RequestParam("url") String url) {
        return shortLinkRemoteService.getTitleByUrl(url);
    }
}
