package com.xiaomi.jstool;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainEntry.class);
    public static Map<File, HashMap<String, JSONObject>> failResultsOfAllMaps = new HashMap<File, HashMap<String, JSONObject>>();
    public static Map<String, JSONObject> stringMap = new HashMap<String, JSONObject>();
    //用于存储base里面的所有国家
    public static void main(String[] args) throws IOException, JSONException {
        String filePath = args[0];
//        String filePath = "D:\\Miot\\MHLocalizableString.js";
//        String filePath = "D:\\Miot\\LocalizedStrings";
//        String filePath = "D:\\Miot\\空净pro台湾";
//        String filePath = "/Users/huamiumiu/Desktop/rn框架/LocalizedStrings";
//        String filePath="/Users/huamiumiu/Desktop/rn框架/pro";
//        String filePath = "/Users/huamiumiu/Desktop/rn框架/MHLocalizableString.js";
        List<File> fileList = new ArrayList();
        ResultAnalyzeHelper resultAnalyzeHelper = new ResultAnalyzeHelper();

        File dataFile = new File(filePath);
        String report = "/report/";
        if(isOsWindows()){
             report = fixPath(report);
            System.out.println("report= "+report);
           report= report.substring(report.indexOf(":")).replace(":","").trim().replace("/","\\");
            System.out.println("report2="+report);
        }
        String basePath = fixPath(dataFile.getParentFile().toString() + report);
        String resultPath = fixPath(basePath + "result.xls");
        String failResultPath = fixPath(basePath + "failResult.txt");
        FileHelper.createFile(fixPath(resultPath));

        StringsHelper.getAllDirsAndFiles(fileList, new File(filePath), "js");
        LOGGER.info("##################### all fileList is:{} ######################", fileList);
        //对每个文件进行处理
        for (int i = 0; i < fileList.size(); i++) {
            File file = fileList.get(i);
            LOGGER.info("##################### 对第 {} 个文件进行处理，total is {} 个文件 #####################", i + 1, fileList.size());
            //选取其中的一个文件
            LOGGER.info("当前文件为：{}", file);
            //将文件转化为string格式来处理
            String fileStringResult = StringsHelper.parseFileToString(file);
            //todo  入口设置在这里，根据文件类型来选择后面的处理方法
            Boolean flag = StringUtils.containsAny(file.getName(), "EN.js", "TW.js");
            if (flag) {
                //空气净化器产品的入口（一个文件一个国家，不同文件的的key不一样，有多个文件）
                String fileName = (file.getName().substring(file.getName().lastIndexOf("-"), file.getName().indexOf(".js")).replace("-", ""));
                JSONObject jb = StringsHelper.parseStringsToJson(fileStringResult);
                stringMap.put(fileName, jb);
            } else {
                //MHLocalizableStrings 产品（一个文件里面包含多个国家，base的key也不一样，一个文件）
                if (file.getName().startsWith("MHL")) {
                    stringMap = StringsHelper.parseStringBaseToMap(fileStringResult);
                } else {
                    Map<String, Integer> exportStringsMap = new HashMap();
                    exportStringsMap.put("stringsExport", 0);
                    List stringsList = new ArrayList();
                    List zhHantList = new ArrayList();
                    List<Map<String, JSONObject>> mapList = new ArrayList();
                    String zh = "zh";
                    String en = "en";
                    String outputPath = fixPath(basePath + file.getName().replace(".js", "") );
                    //将字符串按照格式，划分为stringsList,zhHantList
                    StringsHelper.convertStringFileToListFile(stringsList, zhHantList, fileStringResult, exportStringsMap);
                    resultAnalyzeHelper.convertStringAndZhhantToMap(zhHantList, stringsList, mapList, exportStringsMap);
                    //落盘所有普通数据
                    if(mapList.size()>1) {
                        for (int k = 0; k < mapList.size(); k++) {
                            Map<String,JSONObject> map = mapList.get(k);
                            outputPath =outputPath+ k+".xls";
//                            FileHelper.createFile(outputPath);
                            ExcelHelper.fillExcelWithColor(map, file.getName(), outputPath);
                            outputPath = fixPath(basePath + file.getName().replace(".js", "") );
                        }
                    }else {
                        outputPath += ".xls";
//                        FileHelper.createFile(outputPath);
                        ExcelHelper.fillExcelWithColor(mapList.get(0), file.getName(), outputPath);
                    }
                    resultAnalyzeHelper.outputFailResult(failResultsOfAllMaps, mapList, file, outputPath, zh, en);
                }
            }
        }
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> all all all  all all is finished <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        //针对多个localizedStrings 下多个文件的（一个文件里面包含多个国家）
        if (failResultsOfAllMaps.size() != 0) {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(failResultPath, false));
            BufferedWriter writer0 = new BufferedWriter(outputStreamWriter);
            for (Map.Entry<File, HashMap<String, JSONObject>> entry : failResultsOfAllMaps.entrySet()) {
//                LOGGER.info("file is {},fail result is {}", entry.getKey(), entry.getValue());
                writer0.write(entry.getKey() + "," + entry.getValue());
                writer0.newLine();
                writer0.flush();
            }
        } else if (stringMap.size() != 0) {
            ExcelHelper.fillExcelWithColor(stringMap, "sheet", resultPath);
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
