package org.faicai.d4c.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Describe： History
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-12-26
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("user_history")
public class UserHistory extends BaseEntity{

    @TableId(type = IdType.AUTO)
    private Long historyId;
    private String historyTitle;
    private String historyType;
    private Long connectId;
    private String connectName;
    private Long dbId;
    private String dbName;
    private Long schemaId;
    private String schemaName;
    private Long tableId;
    private String tableName;
    private String sqlContent;
    private String tableRequestParams;
    private String tableSelectTreeState;
    private String tableHiddenFields;

    @TableField(exist = false)
    private boolean canSelect;

}
