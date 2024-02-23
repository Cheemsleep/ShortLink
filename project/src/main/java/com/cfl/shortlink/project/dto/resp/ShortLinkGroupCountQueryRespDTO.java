package com.cfl.shortlink.project.dto.resp;


import lombok.Data;

/**
 * 短链接分组查询返回参数
 */
@Data
public class ShortLinkGroupCountQueryRespDTO {

    /**
     * 分组标识
     * 分组下短链接数量
     */
    private String gid;
    private Integer shortLinkCount;
}
