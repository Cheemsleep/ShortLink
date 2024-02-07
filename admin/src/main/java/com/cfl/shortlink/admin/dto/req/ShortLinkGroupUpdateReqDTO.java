package com.cfl.shortlink.admin.dto.req;


import lombok.Data;

/**
 * 短链接分组修改参数
 */
@Data
public class ShortLinkGroupUpdateReqDTO {
    /**
     * 分组标识gid
     */
    private String gid;
    /**
     * 分组名称
     */
    private String Name;
}
