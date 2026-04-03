CREATE TABLE d4c_user
(
    id          bigserial PRIMARY KEY,
    username    varchar(255) NOT NULL,
    provider    varchar(100)          DEFAULT NULL,
    external_id varchar(100)          DEFAULT NULL,
    account     varchar(100)          DEFAULT NULL,
    password    varchar(255) NOT NULL,
    email       varchar(255) NOT NULL,
    icon_url    varchar(100)          DEFAULT NULL,
    status      integer      NOT NULL DEFAULT 0,
    created_at  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  timestamp             DEFAULT CURRENT_TIMESTAMP,
    created_by  bigint       NOT NULL DEFAULT 0,
    updated_by  bigint                DEFAULT 0,
    deleted     boolean      NOT NULL DEFAULT false,
    UNIQUE (username, email)
);

CREATE INDEX idx_status ON d4c_user (status);

-- 添加列注释
COMMENT ON COLUMN d4c_user.provider IS '用户来源';
COMMENT ON COLUMN d4c_user.external_id IS '用户来源外部id';

-- 创建枚举类型
CREATE
    TYPE db_type_enum AS ENUM (
    'OTHER','JTDS','HSQL','DB2','POSTGRESQL','SQLSERVER','ORACLE','MYSQL',
    'MARIADB','DERBY','HIVE','H2','DM','KINGBASE','GBASE','OCEANBASE',
    'INFORMIX','ODPS','TERADATA','PHOENIX','EDB','KYLIN','SQLITE','ADS',
    'PRESTO','ELASTIC_SEARCH','HBASE','DRDS','CLICKHOUSE','BLINK','ANTSPARK',
    'SPARK','OCEANBASE_ORACLE','POLARDB','ALI_ORACLE','MOCK','SYBASE',
    'HIGHGO','GREENPLUM','GAUSSDB','TRINO','OSCAR','TIDB','TYDB','STARROCKS',
    'GOLDENDB','SNOWFLAKE','REDSHIFT','HOLOGRES','BIGQUERY','IMPALA','DORIS',
    'LEALONE','ATHENA','POLARDBX','SUPERSQL','DATABRICKS','ADB_MYSQL',
    'POLARDB2','SYNAPSE','INGRES','CLOUDSCAPE','TIMESTEN','AS4','SAPDB',
    'KDB','LOG4JDBC','XUGU','FIREBIRDSQL','JSQLCONNECT','JTURBO','INTERBASE',
    'POINTBASE','EDBC','MIMER','TAOSDATA','SUNDB'
    );

-- 创建表
CREATE TABLE database_connect_config
(
    id              bigserial PRIMARY KEY,
    name            varchar(255) NOT NULL,
    user_name       varchar(255) NOT NULL,
    password        varchar(255) NOT NULL,
    port            smallint     NOT NULL,
    host            varchar(255) NOT NULL,
    database_name   varchar(255) NOT NULL,
    schema_name     varchar(100)          DEFAULT NULL,
    max_connections smallint     NOT NULL DEFAULT 10,
    db_type         db_type_enum NOT NULL DEFAULT 'MYSQL',
    created_at      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamp             DEFAULT CURRENT_TIMESTAMP,
    created_by      bigint       NOT NULL DEFAULT 0,
    updated_by      bigint                DEFAULT 0,
    deleted         boolean      NOT NULL DEFAULT false,
    UNIQUE (name, user_name, port, host, database_name, db_type)
);


CREATE TABLE permission
(
    id                bigserial PRIMARY KEY,
    role_id           bigint    NOT NULL,
    resource_id       bigint    NOT NULL,
    can_select        boolean            DEFAULT false,
    can_update        boolean            DEFAULT false,
    can_delete        boolean            DEFAULT false,
    can_insert        boolean            DEFAULT false,
    can_drop          boolean            DEFAULT false,
    can_merge         boolean            DEFAULT false,
    can_create        boolean            DEFAULT false,
    can_alter         boolean            DEFAULT false,
    can_create_index  boolean            DEFAULT false,
    can_drop_index    boolean            DEFAULT false,
    can_referenced    boolean            DEFAULT false,
    can_add           boolean            DEFAULT false,
    can_add_partition boolean            DEFAULT false,
    can_analyze       boolean            DEFAULT false,
    allow             boolean            DEFAULT true,
    created_at        timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        timestamp          DEFAULT CURRENT_TIMESTAMP,
    created_by        bigint    NOT NULL DEFAULT 0,
    updated_by        bigint             DEFAULT 0,
    deleted           boolean   NOT NULL DEFAULT false
);


-- 创建枚举类型
CREATE TYPE resource_type_enum AS ENUM ('CONNECTION', 'DATABASE', 'SCHEMA', 'TABLE', 'COLUMN');

-- 创建表
CREATE TABLE resource
(
    id                  bigserial                                           NOT NULL,
    database_connect_id int8                                                NOT NULL,
    resource_type       varchar(20)  DEFAULT 'DATABASE'::resource_type_enum NOT NULL,
    idx                 int4                                                NULL,
    database_name       varchar(200) DEFAULT NULL::character varying        NULL,
    schema_name         varchar(200) DEFAULT NULL::character varying        NULL,
    table_name          varchar(200) DEFAULT NULL::character varying        NULL,
    column_name         varchar(200) DEFAULT NULL::character varying        NULL,
    description         text                                                NULL,
    created_at          timestamp                                           NOT NULL,
    updated_at          timestamp                                           NULL,
    created_by          int8                                                NOT NULL,
    updated_by          int8                                                NULL,
    deleted             bool         DEFAULT false                          NOT NULL,
    description_ai      text                                                NULL,
    data_type           varchar                                             NULL,
    CONSTRAINT resource_database_connect_id_resource_type_database_name_sc_key UNIQUE (database_connect_id,
                                                                                       resource_type, database_name,
                                                                                       schema_name, table_name,
                                                                                       column_name),
    CONSTRAINT resource_pkey PRIMARY KEY (id)
);

-- 创建 role 表
CREATE TABLE role
(
    id          bigserial PRIMARY KEY,
    role_name   varchar(255) NOT NULL,
    role_code   varchar(100)          DEFAULT NULL,
    description text,
    created_at  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  timestamp             DEFAULT CURRENT_TIMESTAMP,
    created_by  bigint       NOT NULL DEFAULT 0,
    updated_by  bigint                DEFAULT 0,
    deleted     boolean      NOT NULL DEFAULT false,
    UNIQUE (role_name)
);


-- 创建 uc_role 表
CREATE TABLE uc_role
(
    uc_id      bigint    NOT NULL,
    role_id    bigint    NOT NULL,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp          DEFAULT CURRENT_TIMESTAMP,
    created_by bigint    NOT NULL DEFAULT 0,
    updated_by bigint             DEFAULT 0,
    deleted    boolean   NOT NULL DEFAULT false,
    PRIMARY KEY (uc_id, role_id)
);

-- 为 uc_role 表创建索引
CREATE INDEX idx_uc_role_role_id ON uc_role (role_id);

INSERT INTO public.d4c_user (username, provider, external_id, account, "password", email, icon_url, status, created_at,
                             updated_at, created_by, updated_by, deleted)
VALUES ('admin', NULL, NULL, 'admin', '$2a$10$BQKU0B01EqAfGosvTWoJvuoQ0gI5YHxm8zv.6.YbWjWkSmZzfir/i', '', '', 0,
        '2026-03-03 10:46:52.000', '2026-03-25 14:48:16.238', 1, 1, false);