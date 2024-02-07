package com.cfl.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfl.shortlink.admin.dao.entity.GroupDO;
import com.cfl.shortlink.admin.dao.mapper.GroupMapper;
import com.cfl.shortlink.admin.service.GroupService;
import com.cfl.shortlink.admin.util.RandomGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * 短链接分组接口实现层
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    @Override
    public void saveGroup(String groupName) {

        String gid;
        do {
            gid = RandomGenerator.generateRandom();
        } while (!hasGid(gid));

        GroupDO groupDO = GroupDO.builder()
                        .gid(gid)
                        .name(groupName)
                        .sortOrder(0)
                        .build();

        baseMapper.insert(groupDO);
    }

    public boolean hasGid(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                //TODO 设置用户名
                .eq(GroupDO::getUsername, null);

        GroupDO data = baseMapper.selectOne(queryWrapper);
        return data == null;
    }
}
