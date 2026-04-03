package org.faicai.d4c.pojo.vo;

import lombok.Data;

@Data
public class ConnectionVO {

    private Long id;

    private String name;

    private String dbType;


    private boolean canSelect;
    private boolean canUpdate;
    private boolean canDelete;
    private boolean canInsert;
    private boolean canDrop;
    private boolean canMerge;
    private boolean canCreate;
    private boolean canAlter;
    private boolean canCreateIndex;
    private boolean canDropIndex;
    private boolean canReferenced;
    private boolean canAdd;
    private boolean canAddPartition;
    private boolean canAnalyze;
    private boolean allow;

}
