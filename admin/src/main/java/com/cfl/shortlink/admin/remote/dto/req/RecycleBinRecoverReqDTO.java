package com.cfl.shortlink.admin.remote.dto.req;


import lombok.Data;

/**
 * 回收站恢复功能传输层
 */
@Data
public class RecycleBinRecoverReqDTO {
    /**
     * 分组标识
     * 全部短链接
     */
    private String gid;

    private String fullShortUrl;
}
