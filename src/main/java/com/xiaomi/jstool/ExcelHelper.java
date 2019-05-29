package com.xiaomi.jstool;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
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
     * en       ru      fr
     * key1     v11     v12      v13
     * key2     v21     v22      v23
     **/

    public static void fillExcel(Map<String, JSONObject> map, XSSFWorkbook workbook, XSSFSheet sheet) throws IOException, JSONException {
        //把文件名作为列表头（0行，从第一列开始）
        List countryList = new ArrayList();
        List jsonList = new ArrayList();
        int count = 1;
        XSSFRow rowTitle = sheet.createRow(0);
        for (String key : map.keySet()) {
            XSSFCell cellFileName = rowTitle.createCell(count++);
            cellFileName.setCellValue(key);
            countryList.add(key);
        }
        //把map的JONObject 中的key作为行表头，value作为值填充进去（0列，从第一行开始）
        int rowColloum = 1;
//        for (Map.Entry<String, JSONObject> entry : map.entrySet()) {

            JSONObject jsonObject = map.get(countryList.get(0));
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                XSSFRow row = sheet.createRow(rowColloum++);
                XSSFCell cellKey = row.createCell(0);
                String jsonKey = (String) iterator.next();
//                String value = entry.getValue().getString(jsonKey);//这里可以根据实际类型去获取
                cellKey.setCellValue(jsonKey);
                jsonList.add(jsonKey);
                System.out.println("jsonKey" + jsonKey);
            }
//        }

        for (int t = 0; t < jsonList.size(); t++) {
                List jsonValueList = new ArrayList();
            for (int r = 0; r < countryList.size(); r++) {
                String country = countryList.get(r).toString();
                String jsonKey = jsonList.get(t).toString();
                XSSFRow row = sheet.createRow(t + 1);
                if (country.equalsIgnoreCase("ch") || country.equalsIgnoreCase("en")) {
                    XSSFCell cellValue = row.createCell(r + 1);
                    cellValue.setCellValue(map.get(country).getString(jsonKey));
                    jsonValueList.add(map.get(country).getString(jsonKey));
                } else {
                    XSSFCell cellValue = row.createCell(r + 1);
                    cellValue.setCellValue(map.get(country).getString(jsonKey));
                    jsonValueList.add(map.get(country).getString(jsonKey));
                    if (map.get(country).getString(jsonKey).equals(map.get("zh").getString(jsonKey)) ||
                            map.get(country).getString(jsonKey).equals(map.get("en").getString(jsonKey))) {
                        XSSFCellStyle style = workbook.createCellStyle();
                        style.setFillForegroundColor((short) 40);
                        style.setFillPattern(SOLID_FOREGROUND);
                        cellValue.setCellStyle(style);
                    }
                }
            }
            System.out.println("jsonValueList is "+jsonValueList);
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

//        }
    }

    @Test
    public void testJSONObject() throws JSONException {
        String key = null;
        String value = null;
        String jsonString = "{\"rowTitle10\":\"新手引導\",\"rowTitle21\":\"裝置共用\",\"rowTitle20\":\"牙刷標籤\",\"rowTitle12\":\"健康知識\",\"rowTitle23\":\"刪除裝置\",\"rowTitle11\":\"快速指南\",\"rowTitle22\":\"檢查韌體更新\",\"title\":\"設定\",\"navTitle1\":\"使用條款\",\"navTitle2\":\"隱私政策\",\"tip_reRequestData\":\"重新請求數據...\",\"rowTitle14\":\"使用條款和隱私政策\",\"rowTitle13\":\"常見問題\",\"rowTitle15\":\"關於\",\"sectionTitle2\":\"一般設定\",\"tip_connectingDevice\":\"正在連接裝置...\",\"sectionTitle1\":\"mihomeStr\"}";
//然后用Iterator迭代器遍历取值，建议用反射机制解析到封装好的对象中
        JSONObject jsonObject = new JSONObject(jsonString);
        Iterator iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            value = jsonObject.getString(key);
            System.out.println("key is " + key);
            System.out.println("value is " + value);
        }
    }
    @Test
    public void test(){
        List list = new ArrayList();
        list .add(1);
        list .add(2);
        list .add(3);
        list .add(4);
        System.out.println(list.indexOf(3));

    }
    @Test
    public void test05(){
        String s = "zh123456string78922zh123";
        for (int i=0;;i++){
            if(s.startsWith("zh")){
                s= StringUtils.remove(s,"zh");
                System.out.println(s);
            }else {
                break;
            }

        }

    }
    @Test
    public void test06(){
        String s ="12:78\",";
        s = "\""+s.replaceFirst(":","\":");
        System.out.println(s.indexOf("\""));
        System.out.println(s);
    }
    @Test
    public void etst07(){
        String s="\"ko\":{\t  \"title1\": \"초음파 칫솔 장점\",\"title2\": \"초음파 세정 과정\",\"title3\": \"Mi 전동칫솔 헤드\",\"title4\": \"비 스테인리스 금속 칫솔 헤드\",\"title5\": \"Mi 전동칫솔 헤드\",\"title6\": \"Mi 전동칫솔 헤드\",\"title7\": \"3가지 칫솔질 모드\",\"title8\": \"칫솔질 자세 인식\",\"page1_manualToothbrush\": \"수동 칫솔질\",\"page1_manualToothbrush_desc\": \"최대 칫솔질 진동수는 분당 100~200회입니다. 잘못된 칫솔질은 효율성을 떨어뜨리므로 단단한 치석이 제대로 제거되지 않으며 잇몸이 쉽게 상할 수 있습니다.\",\"page1_rotaryElectricToothbrush\": \"회전 전동 칫솔\",\"page1_rotaryElectricToothbrush_desc\": \"칫솔 헤드는 크기가 작아 구강 내부로 깊숙이 들어갈 수 있습니다. 회전하는 동안 회전 전동 칫솔의 진동이 구강 내부 혈액 순환을 촉진시키고 잇몸을 마사지할 수 있습니다. 칫솔질 강도가 일반 칫솔과 비교해 더 약하기 때문에 잇몸 출혈 가능성이 줄어듭니다.\",\"page1_miElectricToothbrush\": \"Mi 전동칫솔\",\"page1_miElectricToothbrush_desc\": \"Mi 전동칫솔은 윗니와 아랫니를 과학적으로 닦을 수 있으며 초음파 세정 과정에서 분당 31,000회 이상 진동합니다. 이를 통해 치아 표면에 붙은 단단한 치석도 제거할 수 있으며 치아 틈새 사이의 치석도 놓치지 않아 잇몸을 더욱 안전하게 보호합니다.\",\"page2_numberOfShock\": \"분당 31,000회 이상 진동\",\"page2_numberOfShock_desc\": \"고주파 진동으로 치약과 물이 섞여 수많은 미세 거품이 만들어져 강력한 세정 능력을 발휘합니다. 거품이 순간적으로 만들어지고 강한 수압의 물줄기가 흘러나와 잇몸을 손상시키지 않고 치석을 효과적으로 제거해 치아가 한층 깨끗해집니다.\",\"page3_brushShape\": \"인체공학적 디자인\",\"page3_brushShape_desc\": \"DuPont의 부드러운 고급 칫솔모를 사용하며 첨단 기술이 적용된 장치와 고밀도 몰딩 과정을 거쳐 칫솔모가 한층 넓어졌습니다. 일반 칫솔의 칫솔모와 비교해 밀도가 40% 이상 늘어났으며 치아에 잘 맞는 인체공학적 디자인을 채택하여 치석과 치태 세정 효과가 크게 개선되었습니다. Mi 전동칫솔 헤드의 칫솔모는 부드러워 잇몸을 안전하게 보호합니다.\",\"page3_tip_changeBrushTime\": \"* 치과 의사들은 3개월마다 칫솔을 교체하도록 권장합니다.\",\"page3_brush_notAbrasiveEffect\": \"거친 브러시\",\"page3_brush_hasPolishedEffect\": \"부드러운 브러시\",\"page4_ordinaryBrushhead\": \"일반 칫솔 헤드\",\"page4_ordinaryBrushhead_desc\": \"일반 칫솔은 칫솔 헤드에 칫솔모를 고정시키기 위해 금속판을 사용합니다. 금속판에 있는 홈은 먼지가 끼고 세균이 번식하기 쉽습니다. 또한, 치약이 금속판을 만나 산화되면서 화학 물질이 생길 수 있으며 이는 칫솔질 도중 입안에 들어가 구강 내부 건강과 위생에 안 좋은 영향을 줄 수 있습니다.\",\"page4_miBrushhead\": \"Mi 전동칫솔 헤드\",\"page4_miBrushhead_desc\": \"Mi 전동칫솔 헤드는 스테인리스 금속이 필요 없는 고밀도 몰딩 기법을 사용합니다. 칫솔모 바닥을 고온으로 녹여 칫솔모와 일체화시킵니다. 칫솔모의 상부가 칫솔모를 칫솔 헤드에 고정시킵니다. 일반 칫솔과 비교해 Mi 전동칫솔 헤드에는 금속판의 화학 물질이나 먼지가 없으며 환경친화적으로 더 위생적이고 안전합니다.\",\"page5_content\": \"DuPont의 부드러운 고급 칫솔모를 채택했으며 인체공학적 디자인으로 치아 형태에 더 잘 맞습니다. 매일 세정이 필요한 사람에게 안성맞춤입니다.\",\"page6_content\": \"DuPont의 부드러운 고급 칫솔모를 사용합니다. 크기가 작아서 구강 내부 깊숙한 곳까지 들어가 안쪽에 가려진 작은 치아도 깨끗하게 닦을 수 있습니다. 흡연이나 잦은 커피, 음주로 인한 치아 변색에도 효과적입니다.\",\"page7_standardMode\": \"표준\",\"page7_standardMode_desc\": \"분당 31,000회 이상의 고주파 진동으로 구강 내부 유체 흐름을 효과적으로 유도해 치아 표면의 치석이나 기타 오염 물질을 신속하게 제거합니다. 매일 세정이 필요한 사람에게 안성맞춤입니다.\",\"page7_gentleMode\": \"부드럽게\",\"page7_gentleMode_desc\": \"마사지 효과가 있어 잇몸이 민감한 사람도 편안하게 이를 닦을 수 있습니다. 잇몸에 출혈이 있거나 염증, 치아가 돌출된 사람도 안심하고 사용할 수 있습니다.\",\"page7_customizePersonality\": \"사용자 정의\",\"page7_customizePersonality_desc1\": \"앱을 사용하면 더욱 다양한 칫솔질 경험을 할 수 있습니다. \",\"page7_customizePersonality_desc2\": \"Mi 전동칫솔을 처음 사용할 때는 초보자 모드에서 시작하고 익숙해지면 다른 모드로 전환해 수동 칫솔에서 전동 칫솔로 점차 옮겨가는 것이 좋습니다.\",\"page8_content\": \"내장된 고정밀 가속 감지기와 Bluetooth 칩은 알고리즘을 활용하여 각 칫솔질의 세정 영역과 유지 시간을 식별하고 이를 저장할 수 있습니다. Bluetooth를 통해 앱과 동기화할 수 있으므로 구강 청결 상태를 확인하고 치과 진료 시 참고 자료를 제공할 수 있습니다.\","
    ;
        System.out.println(s.indexOf("\""));
        System.out.println(s.indexOf("}"));



    }


}


