package org.faicai.d4c.utils;


import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SqlUtil {


    /**
     * 构建AND IN子句
     * 格式: and column in ('value1', 'value2')
     *
     * @param column 列名
     * @param values 字符串集合
     * @return SQL条件语句
     */
    public static String andInClause(String column, Set<String> values) {
        return andInClause(column, values, true);
    }

    /**
     * 构建AND IN子句
     *
     * @param column 列名
     * @param values 字符串集合
     * @param includeAnd 是否包含"and"关键字
     * @return SQL条件语句
     */
    public static String andInClause(String column, Set<String> values, boolean includeAnd) {
        if (values == null || values.isEmpty()) {
            return "";
        }

        String prefix = includeAnd ? " and " : "";
        return values.stream()
                .map(SqlUtil::escapeSqlValue)
                .collect(Collectors.joining(", ",
                        prefix + column + " in (", ")"));
    }

    /**
     * 构建WHERE IN子句
     * 格式: where column in ('value1', 'value2')
     *
     * @param column 列名
     * @param values 字符串集合
     * @return SQL条件语句
     */
    public static String whereInClause(String column, Collection<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }

        return values.stream()
                .map(SqlUtil::escapeSqlValue)
                .collect(Collectors.joining(", ",
                        " where " + column + " in (", ")"));
    }

    /**
     * 构建OR IN子句
     * 格式: or column in ('value1', 'value2')
     *
     * @param column 列名
     * @param values 字符串集合
     * @return SQL条件语句
     */
    public static String orInClause(String column, Set<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }

        return values.stream()
                .map(SqlUtil::escapeSqlValue)
                .collect(Collectors.joining(", ",
                        " or " + column + " in (", ")"));
    }

    /**
     * 构建纯IN子句
     * 格式: in ('value1', 'value2')
     *
     * @param values 字符串集合
     * @return SQL条件语句
     */
    public static String inClause(Collection<String> values) {
        if (values == null || values.isEmpty()) {
            return "in (null)"; // 或者返回"in (null)"，视业务需求
        }

        return values.stream()
                .map(SqlUtil::escapeSqlValue)
                .collect(Collectors.joining(", ", "in (", ")"));
    }

    /**
     * 构建多个AND IN条件
     * 格式: and column1 in ('a','b') and column2 in ('c','d')
     *
     * @param conditions Map<列名, 值集合>
     * @return SQL条件语句
     */
    public static String multipleAndInClauses(Map<String, Set<String>> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Set<String>> entry : conditions.entrySet()) {
            String clause = andInClause(entry.getKey(), entry.getValue(), true);
            if (!clause.isEmpty()) {
                sb.append(clause);
            }
        }
        return sb.toString();
    }

    /**
     * 转义SQL值中的单引号
     *
     * @param value 原始值
     * @return 转义后的值
     */
    public static String escapeSqlValue(String value) {
        if (value == null) {
            return "null";
        }
        // 转义单引号（SQL中的转义方式是双写单引号）
        return "'" + value.replace("'", "''") + "'";
    }

    /**
     * 构建IN子句，支持自定义分隔符
     *
     * @param values 字符串集合
     * @param delimiter 值之间的分隔符
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 格式化后的字符串
     */
    public static String buildClause(Collection<String> values,
                                     String delimiter,
                                     String prefix,
                                     String suffix) {
        if (values == null || values.isEmpty()) {
            return "";
        }

        return values.stream()
                .map(SqlUtil::escapeSqlValue)
                .collect(Collectors.joining(delimiter, prefix, suffix));
    }
}
