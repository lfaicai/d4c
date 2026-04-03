package org.faicai.d4c.constant;

public interface ResponseCode {

    /**
     * 请求成功
     */
    Integer OK = 200;

    /**
     * 系统异常
     */
    Integer SYSTEM_EXCEPTION = 30008;

    /**
     * 必填值缺失
     */
    Integer REQUEST_VALUE_MISSING = 10002;

    /**
     * 值缺失
     */
    Integer VALUE_MISSING = 10003;

    /**
     * 参数校验失败
     */
    Integer VALUE_VALIDATION_FAILED = 10004;

    /**
     * 不存在的数据
     */
    Integer DATA_NOT_EXIST = 11004;

    /**
     * 非法的jwt签名
     */
    Integer JWT_ILLEGAL = 20000;

    /**
     * 刷新token失败，非法token对
     */
    Integer REFRESH_TOKEN_ERROR = 20001;

    /**
     * token已过期
     */
    Integer TOKEN_EXPIRED = 20002;

    /**
     * 缺少token
     */
    Integer MISSING_TOKEN = 20004;

    /**
     * 密码错误
     */
    Integer INCORRECT_PASSWORD = 21000;

    /**
     * 旧密码错误
     */
    Integer OLD_PASSWORD_INCORRECT = 21001;

    /**
     * 用户不存在
     */
    Integer USER_NOT_EXIST = 31000;

    /**
     * 黑名单用户
     */
    Integer BLACKLISTED_USER = 31003;

    /**
     * 用户已经注册
     */
    Integer USER_ALREADY_REGISTERED = 31004;

    /**
     * 用户已被禁用
     */
    Integer USER_DISABLED = 31005;

    /**
     * 升级维护中，暂无法提供服务
     */
    Integer SYSTEM_MAINTENANCE = 33333;

    /**
     * 连接不存在
     */
    Integer CONNECT_NOT_EXIST = 40000;

    /**
     * 获取数据库连接失败
     */
    Integer GET_CONNECT_ERROR = 40001;

    /**
     * 数据库权限不足
     */
    Integer INSUFFICIENT_DATABASE_PERMISSIONS = 40002;

    /**
     * 用户无数据库权限
     */
    Integer NOT_HAVE_DATABASE_PERMISSIONS = 40012;

    /**
     * 用户无schema权限
     */
    Integer NOT_HAVE_SCHEMA_PERMISSIONS = 40013;

    /**
     * 用户无表权限
     */
    Integer NOT_HAVE_TABLE_PERMISSIONS = 40014;

    /**
     * 用户无字段权限
     */
    Integer NOT_HAVE_COLUMN_PERMISSIONS = 40015;

    /**
     * 用户无{0}数据库权限
     */
    Integer NOT_HAVE_CUSTOM_DATABASE_PERMISSIONS = 40016;

    /**
     * 用户无{0}schema权限
     */
    Integer NOT_HAVE_CUSTOM_SCHEMA_PERMISSIONS = 40017;

    /**
     * 用户无{0}表权限
     */
    Integer NOT_HAVE_CUSTOM_TABLE_PERMISSIONS = 40018;

    /**
     * 用户无{0}表{1}字段权限
     */
    Integer NOT_HAVE_CUSTOM_COLUMN_PERMISSIONS = 40019;

    /**
     * SQL命中安全策略被拦截
     */
    Integer UNSAFE_SQL_BLOCKED = 40020;


}
