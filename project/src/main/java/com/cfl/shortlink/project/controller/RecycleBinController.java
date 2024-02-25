package com.cfl.shortlink.project.controller;


import com.cfl.shortlink.project.common.convention.result.Result;
import com.cfl.shortlink.project.common.convention.result.Results;
import com.cfl.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.cfl.shortlink.project.service.RecycleBinService;
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
    private final RecycleBinService recycleBinService;

    /**
     * 保存至回收站
     */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void> save(@RequestBody RecycleBinSaveReqDTO requestParam) {
        recycleBinService.saveRecycleBin(requestParam);
        return Results.success();
    }
}
