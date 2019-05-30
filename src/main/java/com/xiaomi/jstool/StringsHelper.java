package com.xiaomi.jstool;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StringsHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringsHelper.class);

    //从路径获取文件，输出一个string
    public static String getStringFileFromPath(String filePath) throws IOException {
        LOGGER.info("get file from path {}", filePath);
        String fileStringResult = null;
        File file = new File(filePath);
        InputStreamReader isReader = new InputStreamReader(new FileInputStream(file));
        BufferedReader reader = new BufferedReader(isReader);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line.indexOf("//") > 0) {
//                line = line.substring(0, line.indexOf("//"));
                line = "";
            } else if (line.startsWith("import")) {
                line = "";
            } else if (line.indexOf(":") > 0 && (!line.trim().startsWith("\""))) {
                line = ("\"" + line.trim().replaceFirst(":", "\":").trim()).replace("\u00a0", "");
            }
            sb.append(line.trim());
        }
        reader.close();
        fileStringResult = sb.toString();
//        LOGGER.info("get file StringResult: {}", fileStringResult);
        return fileStringResult;
    }

    /**
     * zh_Hant1 = {
     * tip_bluetoothNotOpen: "藍牙未打開",
     * tip_networkError: "網路異常，請檢查網路後重試",
     * alert_title_attention: "提醒"（末尾没有逗号）
     * }
     * <p>
     * <p>
     * "en":{
     * tip_bluetoothNotOpen: "Bluetooth is off",
     * tip_networkError: "Check your network connection and try again",
     * alert_title_attention: "Attention",
     * },(末尾有逗号)
     **/
    //把正文文件分为strings和zh_Hant两种格式，用list存储，对于含有zhHant格式的
    public static void convertStringFileToListFile(List stringsList, List zhHantList, String fileStringResult, Map<String, Integer> exportStringsMap) {
        if (fileStringResult != null) {
            String stringExport = fileStringResult.substring(fileStringResult.indexOf("export"));
            int stringCount = StringUtils.countMatches(stringExport, "string");
            exportStringsMap.put("stringsExport", stringCount);
            String constString = StringUtils.remove(fileStringResult, stringExport);
            String[] constStringArr = constString.split("const");
            for (int i = 0; i < constStringArr.length; i++) {
                if (constStringArr[i].contains("string")) {
                    String subConst = constStringArr[i].substring(constStringArr[i].indexOf("({"), constStringArr[i].indexOf("});")).replace("({", "").trim();
                    stringsList.add(subConst);
                } else if (constStringArr[i].contains("zh_Hant")) {
                    String zh_Hant = constStringArr[i].substring(constStringArr[i].indexOf("zh_Hant"), constStringArr[i].indexOf("};")).trim();
                    if (zh_Hant.endsWith(",")) {
                        StringUtils.reverse(zh_Hant).replaceFirst(",", "");
                        StringUtils.reverse(zh_Hant);
                    }
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

    public static void convertStringFileToListFile(String fileStringResult, File file, Map<File, JSONObject> map) throws JSONException {
        if (fileStringResult != null) {
            String value = fileStringResult.substring(fileStringResult.indexOf("{"), fileStringResult.lastIndexOf("}"));
            if (value.endsWith(",")) {
                value = StringUtils.removeEnd(value, ",");
            }
            JSONObject object = new JSONObject(value);
            map.put(file, object);
        }

    }

    //对strings里面的含有大括号的结构进行处理，存到list
    public static List parseStringToList(String originStrings) {
        List foreignList = new ArrayList();
        String baseStr = null;
        String remove = null;
        if (originStrings != null) {
            for (int tmp = 0; ; tmp++) {
                if (originStrings.startsWith("\"")) {
                    baseStr = originStrings.substring(originStrings.indexOf("\""), originStrings.indexOf("},"));
                    remove = baseStr + "},";
                    //去除大括号前的逗号，确保可以转换成json窜
                    String pop = baseStr.trim();
                    if (pop.endsWith(",")) {
                        pop = StringUtils.reverse(pop).replaceFirst(",", "");
                        pop = StringUtils.reverse(pop);
                    }
                    String strPop = pop + "}";
                    originStrings = StringUtils.remove(originStrings, remove).trim();
                    foreignList.add(strPop);
//                    LOGGER.info("leftString={}", originStrings);
                } else {
                    LOGGER.info("stringsList is done");
                    break;
                }
            }
            LOGGER.info("foreignList is {}", foreignList);
        } else {
            LOGGER.info("originStrings is null");
        }
        return foreignList;
    }


    //todo  这个方法还需要改，文件大了的时候，处理起来很慢。
    //  对strings的整体结构进行处理（包含直接引用和间接引用），存到JSONObject和map里面去
    public static Map parseStringsToMap(List foreignList, Map<String, JSONObject> stringsMap, String zhHant) throws JSONException {
        if (foreignList != null) {
            for (int m = 0; m < foreignList.size(); m++) {
//                JSONObject jsonObject = new JSONObject();
                String foreignString = foreignList.get(m).toString();
                //取地域
                String mapKey = foreignString.substring(0, foreignString.indexOf(":{")).replace("\"", "").trim();
                String valueString = foreignString.substring(foreignString.indexOf(":{")).replace(":{", "{").trim();
                JSONObject mapObject = new JSONObject(valueString);
                stringsMap.put(mapKey, mapObject);
            }
        } else {
            LOGGER.info("input List foreignList is null");
        }
        if (zhHant != null) {
            String key = zhHant.substring(zhHant.indexOf("zh_Hant"), zhHant.indexOf("=")).replace("\"", "").trim();
            String valueString = zhHant.substring(zhHant.indexOf("{")).trim();
            JSONObject jsonObject = new JSONObject(valueString);
            stringsMap.put(key, jsonObject);
        } else {
            LOGGER.info("input zhHant string is null");
        }
//        for (Map.Entry<String, JSONObject> entry : stringsMap.entrySet()) {
//            LOGGER.info("map key={},value={}", entry.getKey(), entry.getValue());
//        }
        return stringsMap;
    }

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



