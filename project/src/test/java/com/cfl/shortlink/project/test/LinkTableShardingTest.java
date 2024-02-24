package com.cfl.shortlink.project.test;

public class LinkTableShardingTest {
    public static final String SQL = "create table t_link_goto_%d\n" +
            "(\n" +
            "    id             bigint auto_increment comment 'ID'\n" +
            "        primary key,\n" +
            "    gid            varchar(32) default 'default' null comment '分组标识',\n" +
            "    full_short_url varchar(128)                  null comment '完整短链接'\n" +
            ");";

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL) + "%n", i);
        }
    }
}
