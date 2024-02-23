package com.cfl.shortlink.project.common.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 有效期类型枚举
 */
@RequiredArgsConstructor
public enum ValidDateTypeEnum {
    /**
     * 永久有效期
     * 自定义
     */
    PERMANENT(0),
    CUSTOM(1);

    @Getter
    private final int type;
}
