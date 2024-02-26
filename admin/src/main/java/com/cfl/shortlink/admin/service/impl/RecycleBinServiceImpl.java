package com.cfl.shortlink.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cfl.shortlink.admin.common.biz.user.UserContext;
import com.cfl.shortlink.admin.common.convention.exception.ServiceException;
import com.cfl.shortlink.admin.common.convention.result.Result;
import com.cfl.shortlink.admin.dao.entity.GroupDO;
import com.cfl.shortlink.admin.dao.mapper.GroupMapper;
import com.cfl.shortlink.admin.remote.ShortLinkRemoteService;
import com.cfl.shortlink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import com.cfl.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.cfl.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.cfl.shortlink.admin.remote.dto.resp.ShortLInkPageRespDTO;
import com.cfl.shortlink.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * URL 接口实现层
 */
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {
    private final GroupMapper groupMapper;

    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    @Override
    public Result<Void> saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        return shortLinkRemoteService.saveRecycleBin(requestParam);
    }

    @Override
    public Result<IPage<ShortLInkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> groupDOList = groupMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(groupDOList)) {
            throw new ServiceException("用户无分组信息");
        }
        requestParam.setGidList(groupDOList.stream().map(GroupDO::getGid).toList());
        return shortLinkRemoteService.pageRecycleBinShortLink(requestParam);
    }
}
