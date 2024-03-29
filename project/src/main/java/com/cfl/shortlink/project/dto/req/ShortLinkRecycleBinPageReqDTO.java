package com.cfl.shortlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * 回收站分页请求参数
 */
@Data
public class ShortLinkRecycleBinPageReqDTO extends Page {
    /**
     * 分组标识
     */
    private List<String> gidList;
}
