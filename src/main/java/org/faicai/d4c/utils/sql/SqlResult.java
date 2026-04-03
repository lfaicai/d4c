package org.faicai.d4c.utils.sql;


import lombok.Data;
import org.faicai.d4c.enums.DataBaseAction;

@Data
public class SqlResult {
    private Object data;
    private DataBaseAction action;

    public SqlResult() {
    }

    public SqlResult(DataBaseAction action, Object data) {
        this.action = action;
        this.data = data;
    }
}
