package com.cfl.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfl.shortlink.project.dao.entity.LinkNetworkStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface LinkNetworkStatsMapper extends BaseMapper<LinkNetworkStatsDO> {
    /**
     * 记录访问网络监控数据
     */
    @Insert("INSERT INTO t_link_network_stats (full_short_url, gid, date, cnt, network, create_time, update_time, del_flag) " +
            "VALUES( #{linkNetworkStats.fullShortUrl}, #{linkNetworkStats.gid}, #{linkNetworkStats.date}, #{linkNetworkStats.cnt}, #{linkNetworkStats.network}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkNetworkStats.cnt};")
    void shortLinkNetworkState(@Param("linkNetworkStats") LinkNetworkStatsDO linkNetworkStatsDO);

}
