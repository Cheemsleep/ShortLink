package com.cfl.shortlink.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfl.shortlink.admin.common.biz.user.UserContext;
import com.cfl.shortlink.admin.common.convention.exception.ServiceException;
import com.cfl.shortlink.admin.common.convention.result.Result;
import com.cfl.shortlink.admin.dao.entity.GroupDO;
import com.cfl.shortlink.admin.dao.mapper.GroupMapper;
import com.cfl.shortlink.admin.remote.ShortLinkActualRemoteService;
import com.cfl.shortlink.admin.remote.dto.req.*;
import com.cfl.shortlink.admin.remote.dto.resp.ShortLInkPageRespDTO;
import com.cfl.shortlink.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 回收站接口实现层
 */
@Service(value = "recycleBinServiceImplByAdmin")
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {

    private final ShortLinkActualRemoteService actualRemoteService;
    private final GroupMapper groupMapper;

    @Override
    public Result<Void> saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        return actualRemoteService.saveRecycleBin(requestParam);
    }

    @Override
    public Result<Page<ShortLInkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> groupDOList = groupMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(groupDOList)) {
            throw new ServiceException("用户无分组信息");
        }
        requestParam.setGidList(groupDOList.stream().map(GroupDO::getGid).toList());
        return actualRemoteService.pageRecycleBinShortLink(requestParam);
    }

    @Override
    public Result<Void> recoverRecycleBin(RecycleBinRecoverReqDTO requestParam) {
        return actualRemoteService.recoverRecycleBin(requestParam);
    }

    @Override
    public Result<Void> removeRecycleBin(RecycleBinRemoveReqDTO requestParam) {
        return actualRemoteService.removeRecycleBin(requestParam);
    }
}
