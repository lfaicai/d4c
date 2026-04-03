package org.faicai.d4c.pojo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.faicai.d4c.pojo.entity.DataBaseConnectConfig;
import org.faicai.d4c.enums.DbDialect;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataBaseConnectConfigDTO extends PageQueryBase<DataBaseConnectConfig> {

    private String name;

    private String host;

    private String databaseName;

    private String schemaName;

    private DbDialect dbType;

}
