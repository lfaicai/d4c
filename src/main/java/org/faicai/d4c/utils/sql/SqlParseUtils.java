package org.faicai.d4c.utils.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import lombok.Data;
import lombok.ToString;
import org.faicai.d4c.constant.ResponseCode;
import org.faicai.d4c.enums.DataBaseAction;
import org.faicai.d4c.enums.DbDialect;
import org.faicai.d4c.enums.ResourceType;
import org.faicai.d4c.exception.BusinessException;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;

import static org.faicai.d4c.enums.DataBaseAction.*;


public class SqlParseUtils {


    public static SqlDefinition sqlParse(SqlInfo sqlInfo) {
        List<SqlDetails> sqlDetailsList = new ArrayList<>();
        final DataBaseAction[] sqlAction = {sqlInfo.getAction()};
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sqlInfo.getSql(), sqlInfo.getDbType());
        for (SQLStatement stmt : stmtList) {
            SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(stmt.getDbType());
            stmt.accept(visitor);
            Map<String, Set<DataBaseAction>> actionMap = new HashMap<>();
            visitor.getTables().forEach((name, tableStat) -> {
                if (sqlAction[0] == null){
                    sqlAction[0] = DataBaseAction.valueOf(tableStat.toString().toUpperCase());
                }
                Set<DataBaseAction> actions = getDataBaseActions(tableStat);
                actionMap.put(name.getName(), actions);

            });
            visitor.getColumns().forEach(column -> {
                Set<DataBaseAction> actions = actionMap.get(column.getTable());
                for (DataBaseAction action : actions) {
                    SqlDetails sqlDetails = parseFullName(column.getTable(), sqlInfo);
                    sqlDetails.setColumnName(column.getName());
                    sqlDetails.setAction(action);
                    sqlDetailsList.add(sqlDetails);
                }
            });
        }
        if (!sqlDetailsList.isEmpty()) {
            sqlInfo.setAction(sqlDetailsList.get(0).getAction());
        }
        return new SqlDefinition(sqlInfo.getSql(), sqlAction[0], sqlDetailsList);
    }

    private static Set<DataBaseAction> getDataBaseActions(TableStat tableStat) {
        Set<DataBaseAction> actions = new HashSet<>();
        if (tableStat.getMergeCount() > 0) {
            actions.add(MERGE);
        }

        if (tableStat.getInsertCount() > 0) {
            actions.add(INSERT);
        }

        if (tableStat.getUpdateCount() > 0) {
            actions.add(UPDATE);
        }

        if (tableStat.getSelectCount() > 0) {
            actions.add(SELECT);
        }

        if (tableStat.getDeleteCount() > 0) {
            actions.add(DELETE);
        }

        if (tableStat.getDropCount() > 0) {
            actions.add(DROP);
        }

        if (tableStat.getCreateCount() > 0) {
            actions.add(CREATE);
        }

        if (tableStat.getAlterCount() > 0) {
            actions.add(ALTER);
        }

        if (tableStat.getCreateIndexCount() > 0) {
            actions.add(CREATE_INDEX);
        }

        if (tableStat.getReferencedCount() > 0) {
            actions.add(REFERENCED);
        }

        if (tableStat.getAddCount() > 0) {
            actions.add(ADD);
        }

        if (tableStat.getAddPartitionCount() > 0) {
            actions.add(ADD_PARTITION);
        }

        if (tableStat.getAnalyzeCount() > 0) {
            actions.add(ANALYZE);
        }

        return actions;
    }

    public static void getName(SQLExpr sqlExpr, List<String> names) {
        if (sqlExpr instanceof SQLPropertyExpr) {
            String name = ((SQLPropertyExpr) sqlExpr).getName();
            names.add(name);
            SQLExpr se = ((SQLPropertyExpr) sqlExpr).getOwner();
            getName(se, names);
        }if (sqlExpr instanceof SQLIdentifierExpr) {
            String name = ((SQLIdentifierExpr) sqlExpr).getName();
            names.add(name);
        }
    }

    @Data
    @ToString
    public static class SqlDefinition{
        private String sql;
        private DataBaseAction action;
        private List<SqlDetails> sqlDetailsList;
        public SqlDefinition() {
        }
        public SqlDefinition(String sql, DataBaseAction action, List<SqlDetails> sqlDetailsList) {
            this.sql = sql;
            this.action = action;
            this.sqlDetailsList = sqlDetailsList;
        }


    }

    @Data
    @ToString
    public static class SqlDetails {

        private String databaseName;
        private String schemaName;
        private String tableName;
        private String columnName;
        private DataBaseAction action;

        public SqlDetails(String databaseName, String schemaName, String tableName) {
            this.databaseName = databaseName;
            this.schemaName = schemaName;
            this.tableName = tableName;
        }

        public String getColumnFullName() {
            String tableName = getTableFullName();
            return (StringUtils.hasText(tableName) ? String.join(".", tableName, columnName) : columnName).toLowerCase();

        }

        public String getTableFullName() {
            String schemaName = getSchemaFullName();
            if (StringUtils.hasText(schemaName)) {
                return String.join(".", schemaName, tableName).toLowerCase();
            }

            if (StringUtils.hasText(databaseName)) {
                return String.join(".", databaseName, tableName).toLowerCase();
            }
            return tableName.toLowerCase();
        }

        public String getSchemaFullName() {
            if (StringUtils.hasText(databaseName) && StringUtils.hasText(schemaName)) {
                return String.join(".", databaseName, schemaName).toLowerCase();
            }
            return schemaName != null ? schemaName.toLowerCase() : null;
        }

        public String getDatabaseFullName() {
            return databaseName.toLowerCase();
        }
    }



    private static SqlDetails parseFullName(String fullName, SqlInfo sqlInfo) {
        String[] parts = fullName.split("\\.");

        String db = null, schema = null, table = null;
        DbDialect dialect = sqlInfo.getDbDialect();
        if (dialect.hasDatabase() && dialect.hasSchema()) {
            // 支持 database + schema + table
            if (parts.length == 3) {
                db = parts[0]; schema = parts[1]; table = parts[2];
            } else if (parts.length == 2) {
                schema = parts[0]; table = parts[1];
            } else {
                table = parts[0];
            }
            if (db == null){
                db = sqlInfo.getDb();
            }
            if (schema == null){
                schema = sqlInfo.getSchema();
            }
        } else if (dialect.hasDatabase()) {
            // 只有 database
            if (parts.length == 2) {
                db = parts[0]; table = parts[1];
            } else {
                table = parts[0];
            }
            if (db == null){
                db = sqlInfo.getDb();
            }
        } else if (dialect.hasSchema()) {
            // 只有 schema
            if (parts.length == 2) {
                schema = parts[0]; table = parts[1];
            } else {
                table = parts[0];
            }
            if (schema == null){
                schema = sqlInfo.getSchema();
            }
        } else {
            // 都没有
            table = parts[0];
        }
        return new SqlDetails(db, schema, table);
    }

    /**
     * 将 SELECT * / a.* 改写为当前用户有权限的字段列表。
     *
     * <p>注意：当 FROM/JOIN 中存在子查询时，为避免语义变化，直接返回原 SQL 不做改写。</p>
     *
     * @param sqlInfo SQL上下文（包含db/dbType/schema/connectId/sql）
     * @param resourceKeyMap 当前用户资源 key 映射（至少需要 {@link ResourceType#COLUMN}）
     * @param tableAllColumnsMap key: tableFullName（小写），value: 该表所有列名集合
     * @param tableColumnsSupplier 当 tableAllColumnsMap 缺失时，用于按表取列名的函数
     */
    public static String rewriteSelectStarToPermittedColumns(
            SqlInfo sqlInfo,
            Map<ResourceType, Set<String>> resourceKeyMap,
            Map<String, Set<String>> tableAllColumnsMap,
            Function<SqlInfo, Set<String>> tableColumnsSupplier
    ) {
        DbType dbType = sqlInfo.getDbType();
        List<SQLStatement> statements = SQLUtils.parseStatements(sqlInfo.getSql(), dbType);
        if (statements.size() != 1) {
            return sqlInfo.getSql();
        }
        SQLStatement statement = statements.get(0);
        if (!(statement instanceof SQLSelectStatement selectStatement)) {
            return sqlInfo.getSql();
        }
        SQLSelectQuery query = selectStatement.getSelect().getQuery();
        if (!(query instanceof SQLSelectQueryBlock queryBlock)) {
            // union / with 等复杂结构暂不改写
            return sqlInfo.getSql();
        }

        List<TableRef> tableRefs = collectTableRefs(queryBlock.getFrom(), sqlInfo);
        if (tableRefs.isEmpty()) {
            return sqlInfo.getSql();
        }

        boolean hasStar = queryBlock.getSelectList().stream().anyMatch(SqlParseUtils::isStarSelectItem);
        if (!hasStar) {
            return sqlInfo.getSql();
        }

        Map<String, List<String>> permittedColumnsByTableKey = new HashMap<>();
        for (TableRef tableRef : tableRefs) {
            List<String> permitted = resolvePermittedColumns(sqlInfo, tableRef, resourceKeyMap, tableAllColumnsMap, tableColumnsSupplier);
            permittedColumnsByTableKey.put(tableRef.tableKeyLower, permitted);
        }

        boolean multiTable = tableRefs.size() > 1;
        List<SQLSelectItem> newSelectItems = new ArrayList<>();
        for (SQLSelectItem item : queryBlock.getSelectList()) {
            if (!isStarSelectItem(item)) {
                newSelectItems.add(item);
                continue;
            }

            StarSpec starSpec = parseStarSpec(item);
            List<TableRef> expandTables;
            if (starSpec.kind == StarKind.ALL) {
                expandTables = tableRefs;
            } else {
                TableRef matched = findTableRefByAliasOrName(tableRefs, starSpec.owner);
                if (matched == null) {
                    // 无法识别 owner 时不做改写，保留原 item
                    newSelectItems.add(item);
                    continue;
                }
                expandTables = List.of(matched);
            }

            for (TableRef t : expandTables) {
                List<String> cols = permittedColumnsByTableKey.getOrDefault(t.tableKeyLower, List.of());
                if (cols.isEmpty()) {
                    // 没有任何字段权限时，直接拦截；避免产生非法 SQL（select from ...）
                    throw new BusinessException(ResponseCode.NOT_HAVE_COLUMN_PERMISSIONS);
                }
                boolean needQualifier = StringUtils.hasText(t.alias) || multiTable;
                String qualifier = StringUtils.hasText(t.alias) ? t.alias : t.tableName;
                for (String col : cols) {
                    SQLExpr expr = needQualifier
                            ? new SQLPropertyExpr(new SQLIdentifierExpr(qualifier), col)
                            : new SQLIdentifierExpr(col);
                    newSelectItems.add(new SQLSelectItem(expr));
                }
            }
        }

        // 兼容不同 Druid 版本：部分版本没有 setSelectList，只能原地修改
        queryBlock.getSelectList().clear();
        queryBlock.getSelectList().addAll(newSelectItems);
        return SQLUtils.toSQLString(statement, dbType);
    }

    /**
     * 为带表别名的列统一补充输出别名，避免分页派生表列名冲突。
     *
     * <p>规则：只要 selectItem 是形如 {@code c.created_at} 这种“带 owner 的列”，并且该 item
     * 没有显式 alias，就补充 {@code AS "c.created_at"}。</p>
     */
    public static String rewriteDuplicateSelectColumnLabels(SqlInfo sqlInfo) {
        DbType dbType = sqlInfo.getDbType();
        List<SQLStatement> statements = SQLUtils.parseStatements(sqlInfo.getSql(), dbType);
        if (statements.size() != 1) {
            return sqlInfo.getSql();
        }
        SQLStatement statement = statements.get(0);
        if (!(statement instanceof SQLSelectStatement selectStatement)) {
            return sqlInfo.getSql();
        }
        SQLSelectQuery query = selectStatement.getSelect().getQuery();
        if (!(query instanceof SQLSelectQueryBlock queryBlock)) {
            return sqlInfo.getSql();
        }

        List<SQLSelectItem> selectList = queryBlock.getSelectList();
        if (selectList == null || selectList.isEmpty()) {
            return sqlInfo.getSql();
        }
        // 收集已使用 alias（避免偶发重复）
        Set<String> used = new HashSet<>();
        for (SQLSelectItem item : selectList) {
            if (item == null) continue;
            if (StringUtils.hasText(item.getAlias())) {
                used.add(item.getAlias().toLowerCase(Locale.ROOT));
            }
        }

        boolean changed = false;
        for (SQLSelectItem item : selectList) {
            if (item == null) continue;
            if (StringUtils.hasText(item.getAlias())) {
                continue;
            }
            SQLExpr expr = item.getExpr();
            if (expr instanceof SQLPropertyExpr propertyExpr) {
                String owner = propertyExpr.getOwnerName();
                String name = propertyExpr.getName();
                if (!StringUtils.hasText(owner) || !StringUtils.hasText(name) || "*".equals(name)) {
                    continue;
                }
                String base = owner + "." + name;
                String unique = toUniqueAlias(base, used);
                item.setAlias(unique);
                used.add(unique.toLowerCase(Locale.ROOT));
                changed = true;
            }
        }

        return changed ? SQLUtils.toSQLString(statement, dbType) : sqlInfo.getSql();
    }

    private static String buildAliasBase(SQLSelectItem item, String currentLabel) {
        SQLExpr expr = item.getExpr();
        if (expr instanceof SQLPropertyExpr propertyExpr) {
            String owner = propertyExpr.getOwnerName();
            String name = propertyExpr.getName();
            if (StringUtils.hasText(owner)) {
                return owner + "." + name;
            }
            return name;
        }
        // 其他表达式（函数/字面量等），用当前 label 做 base，再加后缀由 toUniqueAlias 保证唯一
        return currentLabel;
    }

    private static String toUniqueAlias(String base, Set<String> usedLower) {
        if (!StringUtils.hasText(base)) {
            base = "col";
        }

        String normalizedBase = base.trim();
        String quoted = "\"" + normalizedBase + "\"";
        if (!usedLower.contains(quoted.toLowerCase(Locale.ROOT))) {
            return quoted;
        }
        int i = 2;
        while (true) {
            String candidate = "\"" + normalizedBase + "." + i + "\"";
            if (!usedLower.contains(candidate.toLowerCase(Locale.ROOT))) {
                return candidate;
            }
            i++;
        }
    }

    private static boolean isStarSelectItem(SQLSelectItem item) {
        SQLExpr expr = item.getExpr();
        if (expr instanceof SQLAllColumnExpr) {
            return true;
        }
        if (expr instanceof SQLPropertyExpr propertyExpr) {
            return "*".equals(propertyExpr.getName());
        }
        return false;
    }

    private enum StarKind { ALL, OWNER }

    private static class StarSpec {
        private final StarKind kind;
        private final String owner;

        private StarSpec(StarKind kind, String owner) {
            this.kind = kind;
            this.owner = owner;
        }
    }

    private static StarSpec parseStarSpec(SQLSelectItem item) {
        SQLExpr expr = item.getExpr();
        if (expr instanceof SQLAllColumnExpr) {
            return new StarSpec(StarKind.ALL, null);
        }
        if (expr instanceof SQLPropertyExpr propertyExpr && "*".equals(propertyExpr.getName())) {
            return new StarSpec(StarKind.OWNER, propertyExpr.getOwnerName());
        }
        return new StarSpec(StarKind.ALL, null);
    }

    private static List<TableRef> collectTableRefs(com.alibaba.druid.sql.ast.statement.SQLTableSource from, SqlInfo sqlInfo) {
        if (from == null) return List.of();
        List<TableRef> refs = new ArrayList<>();
        boolean[] hasSubquery = {false};
        collectTableRefs0(from, sqlInfo, refs, hasSubquery);
        // 子查询场景无法安全展开 *
        if (hasSubquery[0]) {
            return List.of();
        }
        return refs;
    }

    private static void collectTableRefs0(
            com.alibaba.druid.sql.ast.statement.SQLTableSource tableSource,
            SqlInfo sqlInfo,
            List<TableRef> out,
            boolean[] hasSubquery
    ) {
        if (tableSource instanceof SQLJoinTableSource join) {
            collectTableRefs0(join.getLeft(), sqlInfo, out, hasSubquery);
            collectTableRefs0(join.getRight(), sqlInfo, out, hasSubquery);
            return;
        }
        if (tableSource instanceof SQLSubqueryTableSource) {
            hasSubquery[0] = true;
            return;
        }
        if (tableSource instanceof SQLExprTableSource exprTableSource) {
            String alias = exprTableSource.getAlias();
            ParsedTableName parsed = parseTableName(exprTableSource.getExpr(), sqlInfo);
            if (parsed == null || !StringUtils.hasText(parsed.table)) {
                return;
            }
            String tableKeyLower = buildTableKeyLower(parsed.db, parsed.schema, parsed.table);
            out.add(new TableRef(parsed.db, parsed.schema, parsed.table, alias, tableKeyLower));
        }
    }

    private static TableRef findTableRefByAliasOrName(List<TableRef> tableRefs, String owner) {
        if (!StringUtils.hasText(owner)) return null;
        String ownerLower = owner.toLowerCase(Locale.ROOT);
        for (TableRef ref : tableRefs) {
            if (StringUtils.hasText(ref.alias) && ref.alias.toLowerCase(Locale.ROOT).equals(ownerLower)) {
                return ref;
            }
        }
        for (TableRef ref : tableRefs) {
            if (ref.tableName.toLowerCase(Locale.ROOT).equals(ownerLower)) {
                return ref;
            }
        }
        return null;
    }

    private static List<String> resolvePermittedColumns(
            SqlInfo sqlInfo,
            TableRef tableRef,
            Map<ResourceType, Set<String>> resourceKeyMap,
            Map<String, Set<String>> tableAllColumnsMap,
            Function<SqlInfo, Set<String>> tableColumnsSupplier
    ) {
        Set<String> allColumns = tableAllColumnsMap.get(tableRef.tableKeyLower);
        if (allColumns == null) {
            Set<String> fetched = tableColumnsSupplier.apply(new SqlInfo(
                    sqlInfo.getConnectId(),
                    tableRef.databaseName,
                    sqlInfo.getDbType().name(),
                    tableRef.schemaName,
                    tableRef.tableName,
                    sqlInfo.getSql()
            ));
            allColumns = fetched;
            tableAllColumnsMap.put(tableRef.tableKeyLower, fetched);
        }

        List<String> ordered = new ArrayList<>(allColumns);
        ordered.sort(String.CASE_INSENSITIVE_ORDER);

        List<String> permitted = new ArrayList<>();
        for (String col : ordered) {
            SqlDetails details = new SqlDetails(tableRef.databaseName, tableRef.schemaName, tableRef.tableName);
            details.setColumnName(col);
            if (!StringUtils.hasText(details.getDatabaseName())) {
                details.setDatabaseName(sqlInfo.getDb());
            }
            String key = details.getColumnFullName();
            if (resourceKeyMap.containsKey(ResourceType.COLUMN) && resourceKeyMap.get(ResourceType.COLUMN).contains(key)) {
                permitted.add(col);
            }
        }
        return permitted;
    }

    private static class TableRef {
        private final String databaseName;
        private final String schemaName;
        private final String tableName;
        private final String alias;
        private final String tableKeyLower;

        private TableRef(String databaseName, String schemaName, String tableName, String alias, String tableKeyLower) {
            this.databaseName = databaseName;
            this.schemaName = schemaName;
            this.tableName = tableName;
            this.alias = alias;
            this.tableKeyLower = tableKeyLower;
        }
    }

    private static class ParsedTableName {
        private final String db;
        private final String schema;
        private final String table;

        private ParsedTableName(String db, String schema, String table) {
            this.db = db;
            this.schema = schema;
            this.table = table;
        }
    }

    private static ParsedTableName parseTableName(SQLExpr expr, SqlInfo sqlInfo) {
        List<String> parts = new ArrayList<>();
        getName(expr, parts);
        if (parts.isEmpty()) return null;
        Collections.reverse(parts);
        String fullName = String.join(".", parts);

        String[] nameParts = fullName.split("\\.");
        String db = null, schema = null, table;
        DbDialect dialect = sqlInfo.getDbDialect();
        if (dialect.hasDatabase() && dialect.hasSchema()) {
            if (nameParts.length == 3) {
                db = nameParts[0];
                schema = nameParts[1];
                table = nameParts[2];
            } else if (nameParts.length == 2) {
                schema = nameParts[0];
                table = nameParts[1];
            } else {
                table = nameParts[0];
            }
            if (db == null) db = sqlInfo.getDb();
            if (schema == null) schema = sqlInfo.getSchema();
        } else if (dialect.hasDatabase()) {
            if (nameParts.length == 2) {
                db = nameParts[0];
                table = nameParts[1];
            } else {
                table = nameParts[0];
            }
            if (db == null) db = sqlInfo.getDb();
        } else if (dialect.hasSchema()) {
            if (nameParts.length == 2) {
                schema = nameParts[0];
                table = nameParts[1];
            } else {
                table = nameParts[0];
            }
            if (schema == null) schema = sqlInfo.getSchema();
        } else {
            table = nameParts[0];
        }
        return new ParsedTableName(db, schema, table);
    }

    private static String buildTableKeyLower(String db, String schema, String table) {
        String key;
        if (StringUtils.hasText(db) && StringUtils.hasText(schema)) {
            key = String.join(".", db, schema, table);
        } else if (StringUtils.hasText(db)) {
            key = String.join(".", db, table);
        } else if (StringUtils.hasText(schema)) {
            key = String.join(".", schema, table);
        } else {
            key = table;
        }
        return key.toLowerCase(Locale.ROOT);
    }

}
