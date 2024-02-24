package com.cfl.shortlink.project.service;


import com.cfl.shortlink.project.common.convention.result.Result;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Url标题接口层
 */
public interface UrlTitleService {
    /**
     * 根据对应url获取标题
     * @param url
     * @return
     */
    String getTitleByUrl(String url);
}
