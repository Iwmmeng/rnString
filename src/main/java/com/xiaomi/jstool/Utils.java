package com.xiaomi.jstool;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    //从路径获取文件
    public static String getStringFileFromPath(String filePath) throws IOException {
        // filePath = "/Users/huamiumiu/Desktop/rn框架/LocalizedStrings/AboutPage.js";

        LOGGER.info("get file from path {}", filePath);
        String fileStringResult = null;
        File file = new File(filePath);
        InputStreamReader isReader = new InputStreamReader(new FileInputStream(file));
        BufferedReader reader = new BufferedReader(isReader);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line.indexOf("//") > 0) {
                line = line.substring(0, line.indexOf("//"));
            }
            if (line.startsWith("import")) {
                line = "";
            }
            sb.append(line);
        }
        reader.close();
        fileStringResult = sb.toString();
//        fileStringResult = sb.substring(sb.indexOf("(") + 1, sb.lastIndexOf("}") + 1).trim();
        LOGGER.info("get file StringResult: {}", fileStringResult);
        return fileStringResult;
    }

    /**
     * zh_Hant1 = {
     * tip_bluetoothNotOpen: "藍牙未打開",
     * tip_networkError: "網路異常，請檢查網路後重試",
     * alert_title_attention: "提醒",
     * }
     * <p>
     * {
     * "en":{
     * tip_bluetoothNotOpen: "Bluetooth is off",
     * tip_networkError: "Check your network connection and try again",
     * alert_title_attention: "Attention",
     * },
     * }
     **/
    //把正文文件分为strings和zh_Hant两种格式，用list存储
    public static void convertStringFileToListFile(List stringsList, List zhHantList, String fileStringResult, Map<String, Integer> exportStringsMap) {
        if (fileStringResult != null) {
            String stringExport = fileStringResult.substring(fileStringResult.indexOf("export"));
            int stringCount = StringUtils.countMatches(stringExport, "string");
            exportStringsMap.put("stringsExport", stringCount);
            String constString = StringUtils.remove(fileStringResult, stringExport);
            String[] constStringArr = constString.split("const");
            for (int i = 0; i < constStringArr.length; i++) {
                if (constStringArr[i].contains("string")) {
                    String subConst = constStringArr[i].substring(constStringArr[i].indexOf("({"), constStringArr[i].indexOf(");")).replace("({", "{").trim();
                    stringsList.add(subConst);
                } else if (constStringArr[i].contains("zh_Hant")) {
                    String zh_Hant = constStringArr[i].substring(constStringArr[i].indexOf("zh_Hant"), constStringArr[i].indexOf("};")).trim();
                    zh_Hant = zh_Hant + "}";
                    zhHantList.add(zh_Hant);
                } else {
                    LOGGER.error("this const is not wanted");
                }
            }
            LOGGER.info("stringsList is {}", stringsList);
            LOGGER.info("zhHantList is {}", zhHantList);

        } else {
            LOGGER.error("input strings is null");
        }
    }

    //对strings里面的含有大括号的结构进行处理，存到list
    public static void parseStringToList(String originStrings, List foreignList, List fantiCNList) {
        String baseStr = null;
        String remove = null;
        String leftString = null;
        if (originStrings != null) {
            //去掉最外层的大括号,这个操作要放在外面做，因为调用了递归，在调用该方法之前，就需要保证该string是去了外层大括号的
//            String originStrings = originStrings.substring(originStrings.indexOf("{"),originStrings.lastIndexOf("}")).replace("{","").trim();
            if (originStrings.startsWith("\"zh-")) {
                //对以zh-开头的做处理
                baseStr = originStrings.substring(originStrings.indexOf("\""), originStrings.indexOf(","));
                remove = originStrings.substring(originStrings.indexOf("\""), originStrings.indexOf(",")) + ",";
                fantiCNList.add(baseStr);
                LOGGER.info("fantiCNList is {}", fantiCNList);
                StringUtils.remove(originStrings, remove);
                leftString = StringUtils.remove(originStrings, remove).trim();
                LOGGER.info("leftString={}", leftString);
                parseStringToList(leftString, foreignList, fantiCNList);
            } else if (originStrings.startsWith("\"")) {
                baseStr = originStrings.substring(originStrings.indexOf("\""), originStrings.indexOf("},"));
                remove = originStrings.substring(originStrings.indexOf("\""), originStrings.indexOf("}")) + "},";
                String strPop = baseStr + "}";
                leftString = StringUtils.remove(originStrings, remove).trim();
                LOGGER.info("leftString={}", leftString);
                foreignList.add(strPop);
                LOGGER.info("foreignList is {}", foreignList);
                parseStringToList(leftString, foreignList, fantiCNList);
            } else {
                LOGGER.info("stringsList is done");
            }
        } else {
            LOGGER.info("originStrings is null");
        }
//        if(originStrings.contains("},")){
//            //去掉最外层的大括号
//                String baseStr = originStrings.substring(originStrings.indexOf("\""),originStrings.indexOf("},"));
//                String remove = originStrings.substring(originStrings.indexOf("\""),originStrings.indexOf("}"))+"},";
//                String strPop = baseStr + "}";
//                resultStringsList.add(strPop);
//      String leftString = StringUtils.remove(originStrings, remove).trim();
//      LOGGER.info("leftString={}", leftString);
//      parseStringsToList(leftString,resultStringsList);
//
//            } else{
//            LOGGER.info("string to list is done");
//        }
    }


    //  对strings的整体结构进行处理（包含直接引用和间接引用），存到JSONObject和map里面去
    public Map parseStringsToMap(List foreignList, List fantiCNList, Map<String, JSONObject> stringsMap, Map<String, JSONObject> zhHantMap) throws JSONException {
        if (foreignList != null) {
            for (int m = 0; m < foreignList.size(); m++) {
                JSONObject jsonObject = new JSONObject();
                String foreignString = foreignList.get(m).toString();
                //取地域
                String mapKey = foreignString.substring(0, foreignString.indexOf(":{")).replace("\"", "").trim();
                String valueString = foreignString.substring(foreignString.indexOf(":{"), foreignString.indexOf("}")).replace(":{", "");
                LOGGER.info("=============valueString is {}", valueString);
                String[] valueArr = valueString.split(",");
                for (int k = 0; k < valueArr.length; k++) {
                    if (valueArr[k].contains(":")) {
                        LOGGER.info("第{}次循环", k);
                        String keyJson = valueArr[k].substring(0, valueArr[k].indexOf(":")).trim();
                        String valueJson = valueArr[k].substring(valueArr[k].indexOf(":")).replace("\"", "").trim();
                        jsonObject.put(keyJson, valueJson);
                        LOGGER.info("stringsMap key is {}", mapKey);
                        LOGGER.info("stringsMap Json value is {}", jsonObject);
                        stringsMap.put(mapKey, jsonObject);
                    } else {
                        LOGGER.info("valueArr is null,continue");
                    }
                }
            }
        } else {
            LOGGER.info("input List foreignList is null");
        }
        if (fantiCNList != null) {
            for (int n = 0; n < fantiCNList.size(); n++) {
                String fantiCNString = fantiCNList.get(n).toString();
                LOGGER.info("fantiCNList is {}", fantiCNList);
                LOGGER.info("fantiCNString is {}", fantiCNString);
                //取地域
                String mapKey = fantiCNString.substring(0, fantiCNString.indexOf(":")).replace("\"", "").trim();
                String valueString = fantiCNString.substring(fantiCNString.indexOf(":")).trim();
                LOGGER.info("======fanti mapKey is {}", mapKey);
                LOGGER.info("======fanti valueString is {}", valueString);
                LOGGER.info("======zhHantMap.get(valueString) is {}", zhHantMap.get(valueString));

                stringsMap.put(mapKey, zhHantMap.get(valueString));
            }
        } else {
            LOGGER.info("inputList fantiCNList is null");
        }
        for (Map.Entry<String, JSONObject> entry : stringsMap.entrySet()) {
            LOGGER.info("map key={},value={}", entry.getKey(), entry.getValue());
        }
        return stringsMap;
    }

    //对zh_Hant的结构进行处理，存到JSONObject和map里面去
    public Map parseHantStringToMap(List zhHantList, Map<String, JSONObject> zhHantMap) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if (zhHantList != null) {
            for (int p = 0; p < zhHantList.size(); p++) {
                String zhHant = zhHantList.get(p).toString();
                String key = zhHant.substring(zhHant.indexOf("zh_Hant"), zhHant.indexOf("=")).replace("\"", "").trim();
                String valueString = zhHant.substring(zhHant.indexOf("{"), zhHant.indexOf("}")).replace("{", "").trim();
                String[] valueArr = valueString.split(",");
                for (int k = 0; k < valueArr.length; k++) {
                    if (valueArr[k].contains(":")) {
                        String keyJson = valueArr[k].substring(0, valueArr[k].indexOf(":")).trim();
                        String valueJson = valueArr[k].substring(valueArr[k].indexOf(":")).replace(":", "").replace("\"", "").trim();
                        jsonObject.put(keyJson, valueJson);
                    } else {
                        LOGGER.info("valueArr[{}] is null,finish the recycle", k);
                    }
                }
                LOGGER.info("zhHantMap key is {}", key);
                LOGGER.info("zhHantMap Json value is {}", jsonObject);
                zhHantMap.put(key, jsonObject);
            }
        } else {
            LOGGER.info("zhHantList  input is not valid ");
        }
        return zhHantMap;
    }


    //对每一个最小的单元进行处理
    //对含有注释的进行处理
    //对含有多个:的进行处理
//    public static Map parseStringsToMap(List fileListResult) throws JSONException {
//        if (fileListResult != null) {
//            Map<String, JSONObject> map = new HashMap<String, JSONObject>();
//            for (int i = 0; i < fileListResult.size(); i++) {
//                JSONObject jsonObject = new JSONObject();
//                LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>开始第{}次，total次数：{}次<<<<<<<<<<<<<<<<<<<", i + 1, fileListResult.size());
//                LOGGER.info("fileListResult is {}",fileListResult);
//                LOGGER.info("current process file list is {}",fileListResult.get(i));
//                String str = fileListResult.get(i).toString();
//                LOGGER.info("str is {}",str);
//                //获取地域国家,存为excel的0行属性头
//                String titleRow = str.substring(str.indexOf("\""), str.indexOf("\":{")).replace("\"", "");
//                LOGGER.info("titleRow:{}", titleRow);
//                //获取国家后对应大括号的字段，存为JSONObject
//                int tmp =str.indexOf(":{");
//                System.out.println(tmp);
//                int tmp2 = str.indexOf("}");
//                System.out.println(tmp2);
//                String string1= str.substring(str.indexOf(":{"), str.indexOf("}"));
//                System.out.println("string1:"+string1);
//                String string2 = string1.replace(":{", "");
//                System.out.println("string2:"+string2);
//                String sub = string2;
//
//
////                String sub = str.substring(str.indexOf(":{"), str.indexOf("}")).replace(":{", "");
//                LOGGER.info("sub:{}", sub);
//                String[] stringArr = sub.split(",");
//                for (String s : stringArr) {
//                    if (s!=null) {
//
//                        String key = null;
//                        String value = null;
//                        //处理多冒号情况
//                        if (StringUtils.countMatches(s, ":") > 1) {
//                            key = s.substring(0, s.indexOf(":")).trim();
//                            value = s.substring(s.indexOf("\""), s.lastIndexOf("\"")).replace("\"", "").trim();
//                            LOGGER.info("多多多多多冒号处理了，key为" + key + ",value为" + value);
//                        } else if (StringUtils.countMatches(s, ":") == 1) {
//                            key = s.split(":")[0].trim();
//                            value = s.split(":")[1].trim().replace("\"", "").trim();
//                            LOGGER.info("单个冒号处理了，key为" + key + ",value为" + value);
//                        } else {
//                            LOGGER.info("不包含冒号{}", s);
//                            continue;
//                        }
//                        jsonObject.put(key, value);
//                    }
//                }
//                map.put(titleRow, jsonObject);
//                LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>");
//                for (Map.Entry entry : map.entrySet()) {
//                    LOGGER.info("entry.getKey: {} ,entry.getValue: {}", entry.getKey(), entry.getValue());
//                }
//            }
//            return map;
//        } else {
//            return null;
//        }
//    }

    public static List<File> getAllDirsAndFiles(List fileNameList, File file, String xmlFileName) {
        if (file.exists() && file.isFile()) {
            if (file.getName().endsWith(xmlFileName)) {
                fileNameList.add(file);
            }
        } else {
            for (File sub : file.listFiles()) {
                getAllDirsAndFiles(fileNameList, sub, xmlFileName);
            }
        }
        return fileNameList;
    }
}



