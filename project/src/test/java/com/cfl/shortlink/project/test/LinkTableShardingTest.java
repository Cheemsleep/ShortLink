package com.cfl.shortlink.project.test;

import com.cfl.shortlink.project.toolkit.DateFormatUtil;

public class LinkTableShardingTest {
    public static final String SQL = "create table t_link_goto_%d\n" +
            "(\n" +
            "    id             bigint auto_increment comment 'ID'\n" +
            "        primary key,\n" +
            "    gid            varchar(32) default 'default' null comment '分组标识',\n" +
            "    full_short_url varchar(128)                  null comment '完整短链接'\n" +
            ");";

    public static final String SQL1 = "alter table t_link_%d\n" +
            "    add del_time bigint default 0 null after update_time;\n" +
            "\n" +
            "alter table t_link_%d\n" +
            "    drop key idx_unique_full_short_url;\n" +
            "\n" +
            "alter table t_link_%d\n" +
            "    add constraint idx_unique_full_short_url\n" +
            "        unique (full_short_url, del_time);";

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL1) + "%n", i, i, i);
        }
    }
}
