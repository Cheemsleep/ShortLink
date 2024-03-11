package com.cfl.shortlink.project.toolkit;


/**
 * 日期时间格式化类
 */
public class DateFormatUtil {
    public static String parseDate(String dateStr) {
        if (dateStr.length() > 19) {
            dateStr = dateStr.substring(0, 19); // 只取前19个字符,即 "yyyy-MM-dd HH:mm:ss"
        }
        return dateStr;
    }
}
