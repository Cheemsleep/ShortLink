package com.cfl.shortlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;


/**
 * 短链接分页请求体对象
 */
@Data
public class ShortLinkPageReqDTO extends Page {
    /**
     * 分组标识
     */
    private String gid;

}
