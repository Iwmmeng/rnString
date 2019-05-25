//package com.xiaomi.jstool;
//
//import org.apache.poi.xssf.usermodel.XSSFCell;
//import org.apache.poi.xssf.usermodel.XSSFCellStyle;
//import org.apache.poi.xssf.usermodel.XSSFRow;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.swing.*;
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//
//import static org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND;
//
//public class ExcelHelper {
//    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelHelper.class);
//
//    /**
//     *          file1   file2   file3
//     * key1     v11     v12     v13
//     * key2     v21     v22      v23
//     **/
//
//    public static void fillExcel(List<File> fileNameList, Map<String, JSONObject> map, XSSFWorkbook workbook, XSSFSheet sheet) throws IOException {
//        //把文件名作为列表头（0行，从第一列开始）
//        int count = 1;
//        XSSFRow rowTitle = sheet.createRow(0);
//        for (int i = 0; i < fileNameList.size(); i++) {
//            XSSFCell cellFileName = rowTitle.createCell(count++);
//            cellFileName.setCellValue(fileNameList.get(i).getParentFile().getName());
//        }
//        //把map的key作为行表头（0列，从第一行开始）
//        int rowColloum = 1;
//        for (Map<String, JSONObject> entry : map.entrySet()) {
//            XSSFRow row = sheet.createRow(rowColloum++);
//            XSSFCell cellKey = row.createCell(0);
//            cellKey.setCellValue(entry.getKey());
//
//            for (int j = 0; j < entry.getValue().size(); j++) {
////                System.out.println("entry.getValue().size()"+entry.getValue().size()+"key"+entry.getKey());
//                if(j==MainEntry.EN ||j==MainEntry.ZH_CN){
//                    XSSFCell cellValue = row.createCell(j + 1);
//                    cellValue.setCellValue(entry.getValue().get(j));
//                }else {
//                    XSSFCell cellValue = row.createCell(j + 1);
//                    cellValue.setCellValue(entry.getValue().get(j));
//                    if(((MainEntry.EN) < entry.getValue().size()) && (MainEntry.ZH_CN < entry.getValue().size())) {
//                        if (entry.getValue().get(j).equals(entry.getValue().get(MainEntry.EN)) ||
//                                entry.getValue().get(j).equals(entry.getValue().get(MainEntry.ZH_CN))) {
//                            XSSFCellStyle style = workbook.createCellStyle();
//                            style.setFillForegroundColor((short) 40);
//                            style.setFillPattern(SOLID_FOREGROUND);
//                            cellValue.setCellStyle(style);
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
//
//
//}
