package com.cfl.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cfl.shortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 短链接统计访问网络实体类
 */
@TableName("t_link_network_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkNetworkStatsDO extends BaseDO {
    /**
     * id
     */
    private Long id;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 日期
     */
    private Date date;

    /**
     * 访问量
     */
    private Integer cnt;

    /**
     * 访问网络
     */
    private String network;


}
