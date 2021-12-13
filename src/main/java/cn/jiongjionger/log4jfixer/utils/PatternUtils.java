package cn.jiongjionger.log4jfixer.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtils {
    /**
     * log4j漏洞利用字符串正则
     * 匹配所有符合${xxx}的内容，避免绕过${jndi:ldap://xxx}或者使用${basedir}等高危方法
     */
    private static final Pattern log4jPattern = Pattern.compile("\\$\\{.*\\}");

    /**
     * 匹配字符串内容
     *
     * @param input 需要匹配的字符串
     * @return 匹配结果，如果字符串包含log4j漏洞利用exp则返回true，否则返回false
     */
    public static boolean match(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        Matcher matcher = log4jPattern.matcher(input);
        return matcher.find();
    }
}
