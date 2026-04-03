package org.faicai.d4c.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.faicai.d4c.enums.DbDialect;

/**
 * @Describe： database config
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-08-05
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("database_connect_config")
public class DataBaseConnectConfig extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String userName;

    private String password;

    private Integer port;

    private String host;

    private String databaseName;

    private String schemaName;

    private Integer maxConnections;

    private DbDialect dbType;



}
