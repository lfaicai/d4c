//package org.faicai.d4c.utils.sql;
//
//
//import java.util.List;
//
//public class Main {
//
//    public static void main(String[] args) {
//        String sql = "CREATE TABLE `uc` (\n" +
//                "  `id` bigint NOT NULL AUTO_INCREMENT,\n" +
//                "  `provider` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,\n" +
//                "  `external_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,\n" +
//                "  `username` varchar(255) NOT NULL,\n" +
//                "  `account` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,\n" +
//                "  `password` varchar(255) NOT NULL,\n" +
//                "  `password_salt` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,\n" +
//                "  `email` varchar(255) NOT NULL,\n" +
//                "  `status` int NOT NULL DEFAULT '0',\n" +
//                "  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n" +
//                "  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
//                "  `icon_url` varchar(100) DEFAULT NULL,\n" +
//                "  PRIMARY KEY (`id`),\n" +
//                "  UNIQUE KEY `idx_username` (`username`),\n" +
//                "  UNIQUE KEY `idx_email` (`email`),\n" +
//                "  KEY `idx_status` (`status`)\n" +
//                ") ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;";
//
//        String sql1 = "select name, age, sex from db1.user";
//        String sql2 = "SELECT * FROM p1.test_t;";
//        String sql3 = "CREATE INDEX users_name_IDX USING BTREE ON users (name);";
//        String sql4 = "select * from user u left join user2 u2 on u2.id=u.id";
//        String sql5 = "select * from user UNION ALL  SELECT * FROM user2 u2 ";
//        String sql6 = "INSERT INTO db1.target_table (id, name, age) SELECT id, name, age FROM db2.target_table WHERE age > 18;";
//        String sql7 = "SELECT ID, PRODUCTION_CODE, APPLICATION_CODE, BASEPATH, DESCRIPTION, CREATION_DATE, CREATED_BY, LAST_UPDATED_BY, LAST_UPDATE_DATE FROM APS.BASE_APPLICATION_B";
//        String sql8 = "SELECT * from  db2.target_table t1 left join db2.target_table t2  on t2.id=t1.id";
//        String sql9 = "select * from orders";
//        String sql10 = "select u.*,y.name from users u left join yourtablename y on u.tt1  = y.create_by ";
//        SqlInfo sqlInfo = new SqlInfo("test", "mysql", "public", sql10);
//        SqlParseUtils.SqlDefinition sqlDefinition = SqlParseUtils.sqlParse(sqlInfo);
//        List<SqlParseUtils.SqlDetails> sqlDetailsList = sqlDefinition.getSqlDetailsList();
//
//        System.out.println(sqlDetailsList);
//    }
//}
