package com.cfl.shortlink.admin.remote.dto.req;


import lombok.Data;

/**
 * 回收站保存功能传输层
 */
@Data
public class RecycleBinSaveReqDTO {
    /**
     * 分组标识
     * 全部短链接
     */
    private String gid;

    private String fullShortUrl;
}
