package com.xiaomi.jstool;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainEntry.class);
    public static Map<File, HashMap<String, JSONObject>> resultMap = new HashMap<File, HashMap<String, JSONObject>>();
    public static Map<File, JSONObject> resultMap2 = new HashMap<File, JSONObject>();

    public static void main(String[] args) throws IOException, JSONException {
//        String filePath="/Users/huamiumiu/Desktop/rn框架/LocalizedStrings";
        String filePath = fixPath(args[0]);
        List<File> fileList = new ArrayList();
        ResultAnalyzeHelper resultAnalyzeHelper = new ResultAnalyzeHelper();
        StringsHelper.getAllDirsAndFiles(fileList, new File(filePath), "js");
        LOGGER.info("##################### all fileList is:{} ######################",fileList);
        //对每个文件进行处理
        for (int i = 0; i < fileList.size(); i++) {
            File file = fileList.get(i);
            LOGGER.info("##################### 对第 {} 个文件进行处理，total is {} 个文件 #####################", i + 1, fileList.size());
           //选取其中的一个文件
            LOGGER.info("当前文件为：{}", file.toString());
            Map<String, Integer> exportStringsMap = new HashMap();
            exportStringsMap.put("stringsExport", 0);
            List stringsList = new ArrayList();
            List zhHantList = new ArrayList();
            List<Map<String, JSONObject>> mapList = new ArrayList();

            //将文件转化为string格式来处理
            String fileStringResult = StringsHelper.getStringFileFromPath(file.toString());
            //todo  入口设置在这里，根据文件类型来选择后面的处理方法
            Boolean flag = StringUtils.containsAny(file.getName(), "EN.js", "TW.js");
            if (flag) {
                StringsHelper.convertStringFileToListFile(fileStringResult, file, resultMap2);
            } else {
                if (file.getName().startsWith("MHL")) {
                    //todo
                } else {
                    //将字符串按照格式，划分为stringsList,zhHantList
                    StringsHelper.convertStringFileToListFile(stringsList, zhHantList, fileStringResult, exportStringsMap);
                    resultAnalyzeHelper.convertStringAndZhhantToMap(zhHantList, stringsList, mapList, exportStringsMap);
                    resultAnalyzeHelper.outputFailResult(resultMap, mapList, file);
                }
            }
        }

        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> all all all  all all is finished <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        if (resultMap.size() != 0) {
            for (Map.Entry<File, HashMap<String, JSONObject>> entry : resultMap.entrySet()) {
                LOGGER.info("file is {},fail result is {}", entry.getKey(), entry.getValue());
            }
        } else {
            LOGGER.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< all is passed,congratulations!!!!! >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
    }


    private static boolean isOsWindows() {
        String osname = System.getProperty("os.name").toLowerCase();
        boolean rt = osname.startsWith("windows");
        return rt;
    }

    public static String fixPath(String path) {
        if (null == path) return path;
        if (path.length() >= 1 && ('/' == path.charAt(0) || '\\' == path.charAt(0))) {
            // 根目录, Windows下需补上盘符.
            if (isOsWindows()) {
                String userdir = System.getProperty("user.dir");
                if (null != userdir && userdir.length() >= 2) {
                    return userdir.substring(0, 2) + path;
                }
            }
        }
        return path;
    }


    @Test
    public void test() {
        String str = "//英文\t\tproduct_name";
        String[] arr = str.split("\t");
        System.out.println(arr.length);
        System.out.println(arr[0]);
        System.out.println(arr[1]);
    }

    @Test
    public void test01() {
        String s1 = "01234567";
        String s2 = "12";
//        StringUtils.remove(s1,s2);
        System.out.println(StringUtils.remove(s1, s2));
    }

    @Test
    public void test3() {
        String s = "1,2\",3\",456";
        String[] arr = s.split("\",");
        for (String s1 : arr) {
            System.out.println(s1);
        }


    }

}
