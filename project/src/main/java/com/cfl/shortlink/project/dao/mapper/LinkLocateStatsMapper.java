package com.cfl.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfl.shortlink.project.dao.entity.LinkLocateStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * 短链接地区统计访问持久层
 */
public interface LinkLocateStatsMapper extends BaseMapper<LinkLocateStatsDO> {
    /**
     * 记录地区访问监控数据
     */
    @Insert("INSERT INTO t_link_locale_stats (full_short_url, gid, date, cnt, country, province, city, adcode, create_time, update_time, del_flag) " +
            "VALUES( #{linkLocateStats.fullShortUrl}, #{linkLocateStats.gid}, #{linkLocateStats.date}, #{linkLocateStats.cnt}, #{linkLocateStats.country}, #{linkLocateStats.province}, #{linkLocateStats.city}, #{linkLocateStats.adcode}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkLocateStats.cnt};")
    void shortLinkLocateState(@Param("linkLocateStats") LinkLocateStatsDO linkLocateStatsDO);
}
