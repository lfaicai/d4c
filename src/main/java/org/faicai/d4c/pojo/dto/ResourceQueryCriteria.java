package org.faicai.d4c.pojo.dto;

import lombok.Data;
import org.faicai.d4c.enums.DataBaseAction;
import org.faicai.d4c.enums.ResourceType;

import java.util.Map;


@Data
public class ResourceQueryCriteria {

    private Long databaseConnectId;

    private ResourceType resourceType;

    private Map<String, DataBaseAction> databaseActions;
}
