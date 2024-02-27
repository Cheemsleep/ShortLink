package com.cfl.shortlink.project.dto.req;


import lombok.Data;

/**
 * 回收站移除功能传输层
 */
@Data
public class RecycleBinRemoveReqDTO {
    /**
     * 分组标识
     * 全部短链接
     */
    private String gid;

    private String fullShortUrl;
}
