package com.xiaomi.jstool;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
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

    //获取文件，输出一个string
    public static String parseFileToString(File file) throws IOException {
        LOGGER.info("file is {}", file);
        String fileStringResult = null;
//        File file = new File(filePath);
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

    /**
     * Map<String, JSONObject>
     * <"zh":{"key1","value1"}>
     */
    //MHLocalizableStrings 产品，对string进行处理，提取符合要求的base串，提取key提取json串,组装成map
    public static Map<String, JSONObject> parseStringToMap(String fileStringResult) throws JSONException {
        Map<String, JSONObject> stringMap = new HashMap<String, JSONObject>();
        String start = fileStringResult.substring(fileStringResult.indexOf("const"), fileStringResult.indexOf("export")).trim();
        String[] strArr = start.split("const ");
        for (String str : strArr) {
            if (str.contains("= {")) {
                String key = str.substring(0, str.indexOf("=")).trim();
                if (StringUtils.containsAny(key, "deBase", "itBase", "frBase", "ruBase", "esBase", "zhBase", "twhkBase", "enBase")) {
                    int begin = str.indexOf("=");
                    int end = str.indexOf("};");
                    String mapValue = str.substring(begin, end).replace("=", "").trim();
                    if (mapValue.endsWith(",")) {
                        StringUtils.removeEnd(mapValue, ",");
                    }
                    JSONObject baseObject = new JSONObject(mapValue + "}");
                    stringMap.put(key, baseObject);
                } else {
                    LOGGER.info("current string is not wanted  ");
                }
            } else {
                LOGGER.info("current string is null");
            }
        }
        return stringMap;
    }


    //                if (StringUtils.containsAny(key, "deBase", "itBase", "frBase", "ruBase", "esBase", "zhBase", "twhkBase", "enBase")) {
//                    int begin = str.indexOf("=");
//                    int end = str.indexOf("};");
//                    String baseMapValue = str.substring(begin, end).replace("=", "").trim();
//                    if (baseMapValue.endsWith(",")) {
//                        StringUtils.removeEnd(baseMapValue, ",");
//                    }
//                    baseMapValue = baseMapValue + "}";
//                    JSONObject baseObject = new JSONObject(baseMapValue);
//                    stringMap.put(key,baseObject);
//                }else {
//                    LOGGER.info("current str is not match");
//                }
//            }else {
//                LOGGER.info("this current str is null，continue");
//            }
//        }
//        for(Map.Entry<String,JSONObject> entry:stringMap.entrySet()){
//            LOGGER.info("map key is {}, value is {}",entry.getKey(),entry.getValue());
//        }
//        return stringMap;
//    }
    //获取map<String,JSONObject>里面JSONObject所有jsoney的并集
    public static Set<String> getAllJsonKeysSet(Map<String, JSONObject> stringMap) {
        Set<String> allJsonKeysSet = new HashSet<String>();
        for (JSONObject jb : stringMap.values()) {
            Iterator iterator = jb.keys();
            while (iterator.hasNext()) {
                String jsonKey = (String) iterator.next();
                allJsonKeysSet.add(jsonKey);
            }
        }
        return allJsonKeysSet;
    }

    //获取map<String,JSONObject>里面所有mapKey
    public static List<String> getMapKeys(Map<String, JSONObject> stringMap) {
        List mapKeyList = new ArrayList();
        for (String mapKey : stringMap.keySet()) {
            mapKeyList.add(mapKey);
        }
        return mapKeyList;
    }

    //落盘map数据
    public static void saveMapToFile(String filePath, Map<String, JSONObject> stringMap) throws IOException, JSONException {
        Set<String> allJsonKeysSet = StringsHelper.getAllJsonKeysSet(stringMap);
        List<String> mapKeyList = StringsHelper.getMapKeys(stringMap);
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false));
        writer.write("keys,");
        for (String s : mapKeyList) {
            writer.write(s + ",");
        }
        writer.newLine();
        for (String jsonKey : allJsonKeysSet) {
            for (int i = 0; i < mapKeyList.size(); i++) {
                String mapKey = mapKeyList.get(i);
                if (!stringMap.get(mapKey).isNull(jsonKey)) {
                    writer.write(stringMap.get(mapKey).getString(jsonKey) + ",");
                } else {
                    writer.write("N/A,");
                }
            }
            writer.newLine();
        }
        writer.close();
    }

    /**
     * Map<String,JSONObject>
     * input  <"zh":{"key1":"value1"}>
     * output  <"key1":{"zh":"value1"}>
     **/
    //map<String,JSONObject> 中JSONObject value的值，若与en或zh列相同，则fail,
    public static Map<String, JSONObject> getFailResultMap(Map<String, JSONObject> stringMap) throws JSONException {
        Map<String, JSONObject> failResultMap = new HashMap<String, JSONObject>();
        int ZHIndex = -1;
        int ENIndex = -1;
        int count = 0;
        List<String> mapKeyList = StringsHelper.getMapKeys(stringMap);
        Set<String> allJsonKeysSet = StringsHelper.getAllJsonKeysSet(stringMap);
        for (String jsonKey : allJsonKeysSet) {
            LOGGER.info("allJsonKeysSet size is {},index is {},jsonKey is {}", allJsonKeysSet.size(), count++, jsonKey);
            List<String> jsonValueList = new ArrayList<String>();
            for (String mapKey : mapKeyList) {
                LOGGER.info("mapKeyList size is {},index is {},mapKey is {}", mapKeyList.size(), mapKeyList.indexOf(mapKey), mapKey);
                if (mapKey.contains("zh")) {
                    ZHIndex = mapKeyList.indexOf(mapKey);
                } else if (mapKey.contains("en")) {
                    ENIndex = mapKeyList.indexOf(mapKey);
                }
                if (!stringMap.get(mapKey).isNull(jsonKey)) {
                    jsonValueList.add(stringMap.get(mapKey).getString(jsonKey));
                } else {
                    jsonValueList.add("N/A");
                }
            }
            LOGGER.info("jsonValueList is {}", jsonValueList);
            if (ZHIndex >= 0 && ENIndex >= 0) {
                JSONObject jsonObject = new JSONObject();
                for (int t = 0; t < jsonValueList.size(); t++) {
                    if (t == ZHIndex || t == ENIndex) {
                        continue;
                    } else {
                        if (jsonValueList.get(t).equals(jsonValueList.get(ZHIndex)) || jsonValueList.get(t).equals(jsonValueList.get(ENIndex))) {
                            jsonObject.put(mapKeyList.get(t), jsonValueList.get(t));
                        } else {
                            LOGGER.info("good job,pass");
                        }
                    }
                }
                if(jsonObject.length()!=0) {
                    failResultMap.put(jsonKey, jsonObject);
                }
            } else {
                LOGGER.info("没有对照参考列 zh/en 列，无法进行比较");
            }
        }
        return failResultMap;
    }

    //落盘fail数据
    public static void saveFailResultMapToFile(Map<String, JSONObject> failResultMap, String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false));
        for (Map.Entry<String, JSONObject> entry : failResultMap.entrySet()) {
            LOGGER.info("key is {},value is {]",entry.getKey(),entry.getValue());
            writer.write(entry.getKey() + "," + entry.getValue());
            writer.newLine();
        }
        writer.close();
    }


    //todo 各个国家的key不一致！！！！！！！
    //MHLocalizableStrings 产品，对string进行处理，提取符合要求的base串，提取key提取json串
    public static List<JSONObject> getBaseJsonList(String fileStringResult, List<String> baseKeyList) throws JSONException {
        List<JSONObject> baseJsonList = new ArrayList<JSONObject>();
        String start = fileStringResult.substring(fileStringResult.indexOf("const"), fileStringResult.indexOf("export")).trim();
        String[] strArr = start.split("const ");
        for (String str : strArr) {
            if (str.contains("= {")) {
                String baseKey = str.substring(0, str.indexOf("=")).trim();
                if (StringUtils.containsAny(baseKey, "deBase", "itBase", "frBase", "ruBase", "esBase", "zhBase", "twhkBase", "enBase")) {
                    int begin = str.indexOf("=");
                    int end = str.indexOf("};");
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
        LOGGER.info("baseKeyList size is {},list is {}", baseKeyList.size(), baseKeyList);
        LOGGER.info("baseJsonList size is {},list  is {}", baseJsonList.size(), baseJsonList);
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

    //用于提取export后面的内容，把string转换成json串，主要用于空净产品
    public static JSONObject parseStringsToJson(String fileStringResult) throws JSONException {
        if (fileStringResult != null && fileStringResult.contains("{")) {
            String start = fileStringResult.substring(fileStringResult.indexOf("{"), fileStringResult.lastIndexOf("}")).trim();
            if (start.endsWith(",")) {
                start = StringUtils.removeEnd(start, ",");
            }
            start = start + "}";
            JSONObject jsonObject = new JSONObject(start);
            LOGGER.info("jsonObject is {}", jsonObject);
            return jsonObject;
        } else {
            LOGGER.info("input string is invalid {}", fileStringResult);
        }
        return null;
    }

    public static Set<String> getJsonKeySet(JSONObject jsonObject) {
        Set<String> jsonKeySet = new HashSet<String>();
        //遍历这个文件的JSONObject，获取key值，存到keySet里面去
        if (jsonObject != null) {
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String jsonKey = (String) iterator.next();
                jsonKeySet.add(jsonKey);
            }
        } else {
            LOGGER.info("input jsonObject is null,sad ");
        }
        return jsonKeySet;
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
                        StringUtils.removeEnd(zh_Hant, ",");
                    }
//                    zh_Hant = zh_Hant + "}";
                    zhHantList.add(zh_Hant + "}");
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



