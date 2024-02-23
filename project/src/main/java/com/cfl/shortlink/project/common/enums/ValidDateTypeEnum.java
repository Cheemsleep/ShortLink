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
    PERMANENT(1),
    CUSTOM(0);

    @Getter
    private final int type;
}
