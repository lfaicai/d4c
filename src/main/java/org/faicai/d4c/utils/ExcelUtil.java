//package org.faicai.d4c.utils;
//
//import org.ttzero.excel.entity.SimpleSheet;
//import org.ttzero.excel.entity.Workbook;
//
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * @Describe：TODO
// * @Author: faicai.lan
// * @Email: lanfaicai@163.com
// * @Date: 2025-12-03
// */
//public class ExcelUtil {
//
//    public static void main(String[] args) {
//        // 准备导出数据
//        List<Object> rows = new ArrayList<>();
//        rows.add(new String[] {"列1", "列2", "列3"});
//        rows.add(new int[] {1, 2, 3, 4});
//        rows.add(new Object[] {5, new Date(), 7, null, "字母", 9, 10.1243});
////        new Workbook()
////                .addSheet(new SimpleSheet<>(rows)) // 添加一个简单工作表
////                .writeTo(Paths.get("F:/excel")); // 导出到F:/excel目录下
//    }
//}
