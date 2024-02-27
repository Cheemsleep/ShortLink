package com.cfl.shortlink.admin.controller;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfl.shortlink.admin.common.convention.result.Result;
import com.cfl.shortlink.admin.remote.dto.req.RecycleBinRecoverReqDTO;
import com.cfl.shortlink.admin.remote.dto.req.RecycleBinRemoveReqDTO;
import com.cfl.shortlink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import com.cfl.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.cfl.shortlink.admin.remote.dto.resp.ShortLInkPageRespDTO;
import com.cfl.shortlink.admin.service.RecycleBinService;
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
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> save(@RequestBody RecycleBinSaveReqDTO requestParam) {
        return recycleBinService.saveRecycleBin(requestParam);
    }

    /**
     * 分页查询回收站短链接
     */
    @GetMapping("/api/short-link/admin/v1/recycle-bin/page")
    public Result<IPage<ShortLInkPageRespDTO>> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        return recycleBinService.pageRecycleBinShortLink(requestParam);
    }

    /**
     * 恢复短链接
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam) {
        return recycleBinService.recoverRecycleBin(requestParam);
    }

    /**
     * 从回收站删除短链接
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/remove")
    public Result<Void> removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO requestParam) {
        return recycleBinService.removeRecycleBin(requestParam);
    }
}
