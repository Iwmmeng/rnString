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
            if (line.indexOf("//") > 0  || line.startsWith("import")) {
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

    //把String类型的文件转换为List结构的文件
    public static List convertStringFileToListFile(List fileListResult,String fileStringResult) {
        if (StringUtils.contains(fileStringResult, ":{") && StringUtils.contains(fileStringResult, "},")) {
//            String titleRow = str.substring(str.indexOf("\""), str.indexOf("\":{")).replace("\"", "");
            String strBase = fileStringResult.substring(fileStringResult.indexOf("\""), fileStringResult.indexOf("},"));
            String remove = fileStringResult.substring(fileStringResult.indexOf("\""), fileStringResult.indexOf("},")) + "},";
            String strPop = strBase + "}";
            fileListResult.add(strPop);
            String leftString = StringUtils.remove(fileStringResult, remove).trim();
            LOGGER.info("leftString={}", leftString);
            convertStringFileToListFile(fileListResult,leftString);
        } else {
            LOGGER.info("string to list is done");
        }
        return fileListResult;
    }

    //对每一个最小的单元进行处理
    //对含有注释的进行处理
    //对含有多个:的进行处理
    public static Map parseFileList(List fileListResult) throws JSONException {
        if (fileListResult != null) {
            Map<String, JSONObject> map = new HashMap<String, JSONObject>();
            for (int i = 0; i < fileListResult.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>开始第{}次，total次数：{}次<<<<<<<<<<<<<<<<<<<", i + 1, fileListResult.size());
                LOGGER.info("fileListResult is {}",fileListResult);
                LOGGER.info("current process file list is {}",fileListResult.get(i));
                String str = fileListResult.get(i).toString();
                LOGGER.info("str is {}",str);
                //获取地域国家,存为excel的0行属性头
                String titleRow = str.substring(str.indexOf("\""), str.indexOf("\":{")).replace("\"", "");
                LOGGER.info("titleRow:{}", titleRow);
                //获取国家后对应大括号的字段，存为JSONObject
                int tmp =str.indexOf(":{");
                System.out.println(tmp);
                int tmp2 = str.indexOf("}");
                System.out.println(tmp2);
                String string1= str.substring(str.indexOf(":{"), str.indexOf("}"));
                System.out.println("string1:"+string1);
                String string2 = string1.replace(":{", "");
                System.out.println("string2:"+string2);
                String sub = string2;


//                String sub = str.substring(str.indexOf(":{"), str.indexOf("}")).replace(":{", "");
                LOGGER.info("sub:{}", sub);
                String[] stringArr = sub.split(",");
                for (String s : stringArr) {
                    if (s!=null) {

                        String key = null;
                        String value = null;
                        //处理多冒号情况
                        if (StringUtils.countMatches(s, ":") > 1) {
                            key = s.substring(0, s.indexOf(":")).trim();
                            value = s.substring(s.indexOf("\""), s.lastIndexOf("\"")).replace("\"", "").trim();
                            LOGGER.info("多多多多多冒号处理了，key为" + key + ",value为" + value);
                        } else if (StringUtils.countMatches(s, ":") == 1) {
                            key = s.split(":")[0].trim();
                            value = s.split(":")[1].trim().replace("\"", "").trim();
                            LOGGER.info("单个冒号处理了，key为" + key + ",value为" + value);
                        } else {
                            LOGGER.info("不包含冒号{}", s);
                            continue;
                        }
                        jsonObject.put(key, value);
                    }
                }
                map.put(titleRow, jsonObject);
                LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>");
                for (Map.Entry entry : map.entrySet()) {
                    LOGGER.info("entry.getKey: {} ,entry.getValue: {}", entry.getKey(), entry.getValue());
                }
            }
            return map;
        } else {
            return null;
        }
    }

    public static List<File> getAllDirsAndFiles(List fileNameList, File file, String xmlFileName) {
        if (file.exists() && file.isFile()) {
            if (file.getName().endsWith(xmlFileName)) {
                fileNameList.add(file);
            }
        } else {
            for (File sub : file.listFiles()) {
                getAllDirsAndFiles(fileNameList,sub, xmlFileName);
            }
        }
        return fileNameList;
    }
}



