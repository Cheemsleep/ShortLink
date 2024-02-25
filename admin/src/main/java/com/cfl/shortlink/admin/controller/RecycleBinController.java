package com.cfl.shortlink.admin.controller;



import com.cfl.shortlink.admin.common.convention.result.Result;
import com.cfl.shortlink.admin.common.convention.result.Results;
import com.cfl.shortlink.admin.remote.ShortLinkRemoteService;
import com.cfl.shortlink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回收站控制层
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    /**
     * 保存至回收站
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> save(@RequestBody RecycleBinSaveReqDTO requestParam) {
        return shortLinkRemoteService.saveRecycleBin(requestParam);
    }
}
