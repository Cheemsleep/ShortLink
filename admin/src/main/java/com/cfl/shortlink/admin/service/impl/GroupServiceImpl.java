package com.cfl.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfl.shortlink.admin.common.biz.user.UserContext;
import com.cfl.shortlink.admin.common.convention.result.Result;
import com.cfl.shortlink.admin.dao.entity.GroupDO;
import com.cfl.shortlink.admin.dao.mapper.GroupMapper;
import com.cfl.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.cfl.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.cfl.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.cfl.shortlink.admin.remote.ShortLinkRemoteService;
import com.cfl.shortlink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.cfl.shortlink.admin.service.GroupService;
import com.cfl.shortlink.admin.util.RandomGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * 短链接分组接口实现层
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    @Override
    public void saveGroup(String name) {

        String gid;
        do {
            gid = RandomGenerator.generateRandom();
        } while (!hasGid(gid));

        GroupDO groupDO = GroupDO.builder()
                        .gid(gid)
                        .username(UserContext.getUsername())
                        .name(name)
                        .sortOrder(0)
                        .build();

        baseMapper.insert(groupDO);
    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);

        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
        Result<List<ShortLinkGroupCountQueryRespDTO>> listResult = shortLinkRemoteService
                .listGroupShortLinkCount(groupDOList.stream().map(GroupDO::getGid).toList());
        List<ShortLinkGroupRespDTO> shortLinkGroupRespDTOList =
                BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
        shortLinkGroupRespDTOList.forEach(each -> {
            Optional<ShortLinkGroupCountQueryRespDTO> first = listResult.getData().stream()
                    .filter(item -> Objects.equals(item.getGid(), each.getGid()))
                    .findFirst();
            first.ifPresent(item -> each.setShortLinkCount(first.get().getShortLinkCount()));
        });
        return shortLinkGroupRespDTOList;
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO requestParam) {
        LambdaUpdateWrapper updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setName(requestParam.getName());
        baseMapper.update(groupDO, updateWrapper);
    }

    @Override
    public void deleteGroup(String gid) {
        LambdaUpdateWrapper updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO, updateWrapper);
    }

    @Override
    public void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam) {
        requestParam.forEach(each -> {
            GroupDO groupDO = GroupDO.builder()
                    .sortOrder(each.getSortOrder())
                    .build();
            LambdaUpdateWrapper updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getGid, each.getGid())
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getDelFlag, 0);
            baseMapper.update(groupDO, updateWrapper);
        });
    }

    public boolean hasGid(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, UserContext.getUsername());

        GroupDO data = baseMapper.selectOne(queryWrapper);
        return data == null;
    }
}
