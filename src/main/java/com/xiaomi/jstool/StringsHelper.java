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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
//            if (line.indexOf("//") > 0 || line.indexOf("////")>0) {
            if (line.contains("//")) {
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
        LOGGER.info("get file StringResult: {}", fileStringResult);
        return fileStringResult;
    }

    //todo 各个国家的key不一致！！！！！！！
    //用于提取相应的base串，主要用于MHLocalizableStrings 产品（一个文件里面包含了多个国家，且export出来的字段有出入）
    public static List<JSONObject> getBaseJsonList(String fileStringResult, List<String> baseKeyList) throws JSONException {
        List<JSONObject> baseJsonList = new ArrayList<JSONObject>();
        String start = fileStringResult.trim().substring(fileStringResult.indexOf("const"), fileStringResult.indexOf("export")).trim();
        String[] strArr = start.split("const ");
        for (String string : strArr) {
            System.out.println(string);
        }
        for (String str : strArr) {
            if (str.contains("= {")) {
//                System.out.println("str is "+str);
//                int num = str.indexOf("=");
//                System.out.println("num= "+num);
                String baseKey = str.substring(0, str.indexOf("=")).trim();
                if (StringUtils.containsAny(baseKey, "deBase", "itBase", "frBase", "ruBase", "esBase", "zhBase", "twhkBase", "enBase")) {
                    //todo 把=号去掉
                    int begin = str.indexOf("=");
                    int end = str.indexOf("};");
//                    int length = str.length();
//                    System.out.println("begin= "+begin);
//                    System.out.println("end= "+end );
//                    System.out.println("length= "+length);
                    String baseMapValue = str.substring(begin, end).replace("=", "").trim();
                    if (baseMapValue.endsWith(",")) {
                        StringUtils.removeEnd(baseMapValue, ",");
                    }
                    baseMapValue = baseMapValue + "}";
                    JSONObject baseObject = new JSONObject(baseMapValue);
                    baseKeyList.add(baseKey);
                    baseJsonList.add(baseObject);
                } else {
                    LOGGER.info("current str is not match");
                    }
            } else {
                LOGGER.info("this current str is null，continue");
            }
        }
        LOGGER.info("baseKeyList size is {},list is {}",baseKeyList.size(),baseKeyList);
        LOGGER.info("baseJsonList size is {},list  is {}",baseJsonList.size(),baseJsonList);
        return baseJsonList;

    }

    public static void getAllJsonKeys(JSONObject jsonObject, Set<String> keySet) {
        if (jsonObject != null) {
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String jsonKey = (String) iterator.next();
                keySet.add(jsonKey);
            }
        } else {
            LOGGER.info("input jsonObject is null");
        }
    }


    //用于提取export后面的内容，主要用于空净产品
    public static JSONObject parseStringsToJson(String fileStringResult, Set<String> keySet) throws JSONException {
        if (fileStringResult != null && fileStringResult.contains("{")) {
            String start = fileStringResult.substring(fileStringResult.indexOf("{"), fileStringResult.lastIndexOf("}")).trim();
            if (start.endsWith(",")) {
                start = StringUtils.removeEnd(start, ",");
            }
            start = start + "}";
            LOGGER.info("jsonObject from string : {}", start);
            JSONObject jsonObject = new JSONObject(start);
            //遍历这个文件的JSONObject，获取key值，存到keySet里面去
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String jsonKey = (String) iterator.next();
                keySet.add(jsonKey);
            }
            return jsonObject;
        } else {
            LOGGER.info("input string is invalid {}", fileStringResult);
        }
        return null;
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



