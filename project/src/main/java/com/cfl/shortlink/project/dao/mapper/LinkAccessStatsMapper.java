package com.cfl.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfl.shortlink.project.dao.entity.LinkAccessStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * 短链接基础访问监控持久层
 */
public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {

    /**
     * 记录基础访问监控数据
     */
    @Insert("INSERT INTO t_link_access_stats " +
            "(" +
            "    full_short_url, gid, date, pv, uv, uip, hour, weekday, create_time, update_time, del_flag" +
            ")\n" +
            "VALUES(" +
            "          #{linkAccessStats.fullShortUrl}, " +
            "          #{linkAccessStats.gid}, " +
            "          #{linkAccessStats.date}, " +
            "          #{linkAccessStats.pv}, " +
            "          #{linkAccessStats.uv}, " +
            "          #{linkAccessStats.uip}, " +
            "          #{linkAccessStats.hour}, " +
            "          #{linkAccessStats.weekday}, " +
            "          NOW(), " +
            "          NOW(), " +
            "          0" +
            ")\n" +
            "ON DUPLICATE KEY UPDATE pv = #{linkAccessStats.pv} + 1, uv = #{linkAccessStats.uv} + 2, uip = #{linkAccessStats.uip} + 3;")
    void shortLinkStats(@Param("linkAccessStats") LinkAccessStatsDO linkAccessStats);

}
