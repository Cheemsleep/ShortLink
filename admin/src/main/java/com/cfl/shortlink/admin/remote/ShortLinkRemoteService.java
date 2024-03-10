package com.cfl.shortlink.admin.remote;


import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfl.shortlink.admin.common.convention.result.Result;
import com.cfl.shortlink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import com.cfl.shortlink.admin.remote.dto.resp.*;
import com.cfl.shortlink.admin.remote.dto.req.*;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短链接中台远程调用服务
 */
public interface ShortLinkRemoteService {

    /**
     * 创建短链接远程调用
     * @param requestParam
     * @return
     */
    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {
        String resultBodyStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/create", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }

    /**
     * 批量创建短链接
     * @param requestParam 批量创建短链接请求参数
     * @return 短链接批量创建响应
     */
    default Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam) {
        String resultBody = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/create/batch", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBody, new TypeReference<>() {
        });
    }

    /**
     * 短链接分页远程调用
     * @param requestParam
     * @return
     */
    default Result<IPage<ShortLInkPageRespDTO>> pageShortLink(@SpringQueryMap ShortLinkPageReqDTO requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", requestParam.getGid());
        requestMap.put("orderTag", requestParam.getOrderTag());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/page", requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<Result<IPage<ShortLInkPageRespDTO>>>() {
        });
    }

    /**
     * 查询短链接下的分组数量
     * @param requestParam
     * @return
     */
    default Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(List<String> requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("requestParam", requestParam);
        String resultCountStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/count", requestMap);
        return JSON.parseObject(resultCountStr, new TypeReference<>() {
        });
    }

    /**
     * 修改短链接
     * @param requestParam
     */
    default Result<Void> updateShortLink(ShortLInkUpdateReqDTO requestParam) {
        String resultBody = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/update", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBody, new TypeReference<>() {
        });
    }

    /**
     * 根据对应url获取标题
     * @param url
     * @return
     */
    default Result<String> getTitleByUrl(String url) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("url", url);
        String resultTitle = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/title", requestMap);
        return JSON.parseObject(resultTitle, new TypeReference<>() {
        });
    }

    /**
     * 保存至回收站
     * @param requestParam
     */
    default Result<Void> saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        String resultBody = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/save", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBody, new TypeReference<>() {
        });
    }

    /**
     * 分页查询回收站短链接
     * @param requestParam
     */
    default Result<IPage<ShortLInkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gidList", requestParam.getGidList());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/page", requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }

    /**
     * 回收站恢复短链接
     * @param requestParam
     * @return
     */
    default Result<Void> recoverRecycleBin(RecycleBinRecoverReqDTO requestParam) {
        String resultBody = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/recover", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBody, new TypeReference<>() {
        });
    }

    /**
     * 回收站删除短链接
     * @param requestParam
     * @return
     */
    default Result<Void> removeRecycleBin(RecycleBinRemoveReqDTO requestParam) {
        String resultBody = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/remove", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBody, new TypeReference<>() {
        });
    }

    /**
     * 访问单个短链接指定时间内监控数据
     * @param requestParam
     * @return 短链接监控信息
     */
    default Result<ShortLinkStatsRespDTO> oneShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("fullShortUrl", requestParam.getFullShortUrl());
        requestMap.put("gid", requestParam.getGid());
        requestMap.put("startDate", requestParam.getStartDate());
        requestMap.put("endDate", requestParam.getEndDate());
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats", requestMap);
        return JSON.parseObject(resultBodyStr, Result.class);
    }

    /**
     * 访问分组下所有链接指定时间内监控数据
     * @param requestParam
     * @return 短链接监控信息
     */
    default Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", requestParam.getGid());
        requestMap.put("startDate", requestParam.getStartDate());
        requestMap.put("endDate", requestParam.getEndDate());
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats/group", requestMap);
        return JSON.parseObject(resultBodyStr, Result.class);
    }



    /**
     * 访问单个短链接指定时间内访问记录监控数据
     * @param requestParam
     * @return 短链接监控记录信息
     */
    default Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("fullShortUrl", requestParam.getFullShortUrl());
        requestMap.put("gid", requestParam.getGid());
        requestMap.put("startDate", requestParam.getStartDate());
        requestMap.put("endDate", requestParam.getEndDate());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/access-record", requestMap);
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }

    /**
     * 访问分组短链接指定时间内访问记录监控数据
     * @param requestParam
     * @return 分组短链接监控记录信息
     */
    default Result<IPage<ShortLinkGroupStatsAccessRecordRespDTO>> shortLinkStatsAccessGroupRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", requestParam.getGid());
        requestMap.put("startDate", requestParam.getStartDate());
        requestMap.put("endDate", requestParam.getEndDate());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/access-record/group", requestMap);
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }
}
