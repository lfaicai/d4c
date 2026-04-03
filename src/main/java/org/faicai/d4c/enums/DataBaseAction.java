package org.faicai.d4c.enums;

import lombok.Getter;

@Getter
public enum DataBaseAction {
    SELECT("SELECT"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    INSERT("INSERT"),
    DROP("DROP"),
    MERGE("MERGE"),
    CREATE("CREATE"),
    ALTER("ALTER"),
    CREATE_INDEX("CREATE_INDEX"),
    DROP_INDEX("DROP_INDEX"),
    REFERENCED("REFERENCED"),
    ADD("ADD"),
    ADD_PARTITION("ADD_PARTITION"),
    ANALYZE("ANALYZE");



    private final String code;

    DataBaseAction(String code) {
        this.code = code;
    }
}
