package com.xiaomi.jstool;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND;

public class ExcelHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelHelper.class);

    /**
     *          en       ru      fr
     * key1     v11     v12      v13
     * key2     v21     v22      v23
     **/

    public static void fillExcel( Map<String,JSONObject> map, XSSFWorkbook workbook, XSSFSheet sheet) throws IOException, JSONException {
        //把文件名作为列表头（0行，从第一列开始）
        List countryList = new ArrayList();
        List jsonList = new ArrayList();
        int count = 1;
        XSSFRow rowTitle = sheet.createRow(0);
        for (String key:map.keySet()) {
            XSSFCell cellFileName = rowTitle.createCell(count++);
            cellFileName.setCellValue(key);
            countryList.add(key);
        }
        //把map的JONObject 中的key作为行表头，value作为值填充进去（0列，从第一行开始）
        int rowColloum = 1;
        for (Map.Entry<String,JSONObject> entry : map.entrySet()) {
            Iterator iterator = entry.getValue().keys();
            if (iterator.hasNext()) {
                XSSFRow row = sheet.createRow(rowColloum++);
                XSSFCell cellKey = row.createCell(0);
                String jsonKey = (String) iterator.next();
//                String value = entry.getValue().getString(jsonKey);//这里可以根据实际类型去获取
                cellKey.setCellValue(jsonKey);
                jsonList.add(jsonKey);
            } else {
                break;
            }
        }

        for(int t=0;t<jsonList.size();t++){
         for(int r =0;r<countryList.size();r++){
                String country = countryList.get(r).toString();
                String jsonKey = jsonList.get(t).toString();
                XSSFRow row = sheet.createRow(t+1);
                if(country.equalsIgnoreCase("ch")||country.equalsIgnoreCase("en")){
                    XSSFCell cellValue = row.createCell(r + 1);
                    cellValue.setCellValue(map.get(country).getString(jsonKey));
                }else{
                    XSSFCell cellValue = row.createCell(r + 1);
                    cellValue.setCellValue(map.get(country).getString(jsonKey));
                    if(map.get(country).getString(jsonKey).equals(map.get("zh").getString(jsonKey)) ||
                            map.get(country).getString(jsonKey).equals(map.get("en").getString(jsonKey)) ){
                        XSSFCellStyle style = workbook.createCellStyle();
                        style.setFillForegroundColor((short) 40);
                        style.setFillPattern(SOLID_FOREGROUND);
                        cellValue.setCellStyle(style);
                    }
                }
            }

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
        }
    }




}
