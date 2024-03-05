package com.cfl.shortlink.admin.test;

public class UserTableShardingTest {

    public static final String SQL = "CREATE TABLE `t_link_stats_today_%d` (\n" +
            "                                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',\n" +
            "                                      `gid` varchar(32) DEFAULT 'default' COMMENT '分组标识',\n" +
            "                                      `full_short_url` varchar(128) DEFAULT NULL COMMENT '短链接',\n" +
            "                                      `date` date DEFAULT NULL COMMENT '日期',\n" +
            "                                      `today_pv` int(11) DEFAULT '0' COMMENT '今日PV',\n" +
            "                                      `today_uv` int(11) DEFAULT '0' COMMENT '今日UV',\n" +
            "                                      `today_uip` int(11) DEFAULT '0' COMMENT '今日IP数',\n" +
            "                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',\n" +
            "                                      `update_time` datetime DEFAULT NULL COMMENT '修改时间',\n" +
            "                                      `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',\n" +
            "                                      PRIMARY KEY (`id`),\n" +
            "                                      UNIQUE KEY `idx_unique_full-short-url` (`full_short_url`) USING BTREE\n" +
            ") ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;";

    public static final String SQL1 = "alter table t_link_%d\n" +
            "    change today_pv total_pv int null comment '历史PV';\n" +
            "\n" +
            "alter table t_link_%d\n" +
            "    change today_uv total_uv int null comment '历史UV';\n" +
            "\n" +
            "alter table t_link_%d\n" +
            "    change today_uip total_uip int null comment '历史UIP';";

    public static void main(String[] args) {
        for (int i = 1; i < 16; i++) {
            System.out.printf((SQL1) + "%n", i, i, i);
        }
    }
}
