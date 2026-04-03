package org.faicai.d4c.pojo.dto;

import lombok.Data;
import org.faicai.d4c.enums.ResourceType;

/**
 * @Describe： findChildrenNodesByRoleId 参数
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-11-10
 */
@Data
public class RoleResourceNodesDTO {

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 连接id
     */
    private Long connectId;

    /**
     * 节点类型
     */
    private ResourceType nodeType;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 数据库名称
     */
    private String dbName;
}
