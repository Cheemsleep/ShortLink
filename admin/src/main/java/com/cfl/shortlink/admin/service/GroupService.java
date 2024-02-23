package com.cfl.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cfl.shortlink.admin.dao.entity.GroupDO;
import com.cfl.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.cfl.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.cfl.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

/**
 * 短链接分组接口层
 */
public interface GroupService extends IService<GroupDO> {

    /**
     * 新增短链接分组
     * @param GroupName
     */
    void saveGroup(String GroupName);

    /**
     * 新增短链接分组
     * @param username 用户名
     * @param GroupName 分组名
     */
    void saveGroup(String username ,String GroupName);

    /**
     * 查询用户短链接分组集合
     * @return 短链接用户分组集合
     */
    List<ShortLinkGroupRespDTO> listGroup();

    /**
     * 修改短链接分组
     * @param requestParam
     */
    void updateGroup(ShortLinkGroupUpdateReqDTO requestParam);

    /**
     * 删除短链接分组
     * @param gid
     */
    void deleteGroup(String gid);

    /**
     * 修改短链接分组排序
     * @param requestParam
     */
    void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam);
}
