package cn.jiongjionger.log4jfixer.utils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.lookup.Interpolator;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;

public class Log4jFixerUtils {
    private static Field lookupsField = null;
    private static Field configField = null;

    static {
        initInterpolatorField();
    }

    /**
     * 从Interpolator类中获取成员lookups并设置为可访问
     */
    private static void initInterpolatorField() {
        try {
            lookupsField = getLookupsField();
            if (lookupsField != null) {
                lookupsField.setAccessible(true);
            }
            configField = getConfigField();
            if (configField != null) {
                configField.setAccessible(true);
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * 根据不同log4j版本获取对应的lookups
     *
     * @return 获取的lookups，如果获取失败则返回null
     */
    @Nullable
    private static Field getLookupsField() {
        Field field = getDeclaredFieldWithSuperClass(Interpolator.class, "lookups");
        if (field == null) {
            field = getDeclaredFieldWithSuperClass(Interpolator.class, "strLookupMap");
        }
        return field;
    }

    /**
     * 根据不同log4j版本获取对应的config
     *
     * @return 获取的config，如果获取失败则返回null
     */
    @Nullable
    private static Field getConfigField() {
        Field field = getDeclaredFieldWithSuperClass(PatternLayout.class, "configuration");
        if (field == null) {
            field = getDeclaredFieldWithSuperClass(PatternLayout.class, "config");
        }
        return field;
    }

    /**
     * 从指定类及其父类获取对应的Field
     *
     * @param clazz     指定的类
     * @param fieldName 成员名称
     * @return 返回获取的成员，如果不存在则返回null
     */
    @Nullable
    private static Field getDeclaredFieldWithSuperClass(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignore) {
            }
            currentClass = currentClass.getSuperclass();
        }
        return null;
    }

    /**
     * 查找Interpolator类
     *
     * @return 如果存在Interpolator类则返回true，否则返回false
     */
    public static boolean findInterpolator() {
        boolean status = false;
        try {
            Class.forName("org.apache.logging.log4j.core.lookup.Interpolator");
            status = true;
        } catch (ClassNotFoundException ignore) {
        }
        return status;
    }

    /**
     * 尝试修复
     *
     * @return 修复成功则返回true，否则返回false
     */
    public static boolean tryFix() {
        if (lookupsField == null) {
            Bukkit.getLogger().warning("[EastLandLog4jFixer] LookupsField is null!");
            return false;
        }
        if (configField == null) {
            Bukkit.getLogger().warning("[EastLandLog4jFixer] ConfigField is null!");
            return false;
        }
        boolean status = false;
        try {
            final org.apache.logging.log4j.core.Logger rootLogger = (org.apache.logging.log4j.core.Logger)
                    LogManager.getRootLogger();
            final StrLookup strLookup = rootLogger
                    .getContext()
                    .getConfiguration()
                    .getStrSubstitutor()
                    .getVariableResolver();
            if (strLookup instanceof Interpolator) {
                clearLookups((Interpolator) strLookup);
            }
            final Map<String, Appender> appenders = rootLogger.getAppenders();
            if (!appenders.isEmpty()) {
                for (Map.Entry<String, Appender> entry : appenders.entrySet()) {
                    final Appender appender = entry.getValue();
                    final Layout<? extends Serializable> layout = appender.getLayout();
                    if (!(layout instanceof PatternLayout)) {
                        continue;
                    }
                    final PatternLayout patternLayout = (PatternLayout) layout;
                    final Configuration config = (Configuration) configField.get(patternLayout);
                    final StrLookup patternLayoutStrLookup = config.getStrSubstitutor().getVariableResolver();
                    if (patternLayoutStrLookup instanceof Interpolator) {
                        clearLookups((Interpolator) patternLayoutStrLookup);
                    }
                }
            }
            status = true;
        } catch (Exception ignore) {
        }
        return status;
    }

    /**
     * 清空指定Interpolator类中的lookups（Map）变量
     */
    private static void clearLookups(Interpolator interpolator) {
        try {
            Bukkit.getLogger().info("[EastLandLog4jFixer] Try to clear lookups..");
            Object lookupsObject = lookupsField.get(interpolator);
            if (lookupsObject instanceof Map) {
                ((Map<?, ?>) lookupsObject).clear();
            }
            Bukkit.getLogger().info("[EastLandLog4jFixer] Successfully clear lookups!");
        } catch (Exception ignore) {
            Bukkit.getLogger().warning("[EastLandLog4jFixer] Try to clear lookups failed!");
        }
    }
}
