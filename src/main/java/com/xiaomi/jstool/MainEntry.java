package com.xiaomi.jstool;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainEntry.class);
    public static Map<File, HashMap<String, JSONObject>> failResultsOfAllMaps = new HashMap<File, HashMap<String, JSONObject>>();
    public static Set<String> totalKeySet = new HashSet<String>();
    public static List<JSONObject> jsonList = new ArrayList<JSONObject>();
    public  static List<String> fileNameList = new ArrayList<String>();

    public static void main(String[] args) throws IOException, JSONException {
//        String filePath="/Users/huamiumiu/Desktop/rn框架/LocalizedStrings";
//        String filePath="/Users/huamiumiu/Desktop/rn框架/pro";
        String filePath="/Users/huamiumiu/Desktop/rn框架/MHLocalizableString.js";
        File dataFile = new File(filePath);
        String basePath = dataFile.getParentFile().toString()+"/report/";
        String resultPath = fixPath(basePath + "result.txt");
        FileHelper.createFile(fixPath(resultPath));

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
            //将文件转化为string格式来处理
            String fileStringResult = StringsHelper.getStringFileFromPath(file.toString());
            //todo  入口设置在这里，根据文件类型来选择后面的处理方法
            Boolean flag = StringUtils.containsAny(file.getName(), "EN.js", "TW.js");
            if (flag) {
                //空气净化器产品的入口
                Set<String> keySet = new HashSet<String>();
                fileNameList.add(file.getName().substring(file.getName().lastIndexOf("-"),file.getName().indexOf(".js")).replace("-",""));
                JSONObject jb = StringsHelper.parseStringsToJson(fileStringResult,keySet);
                totalKeySet.addAll(keySet);
                jsonList.add(jb);

            } else {
                //MHLocalizableStrings 产品（一个文件里面包含了多个国家，且export出来的字段有出入）
                if (file.getName().startsWith("MHL")) {
                    //todo
                    List<Map<String, JSONObject>> mapList = new ArrayList();
                    List<String> baseMapKeyList = new ArrayList<String>();
                    String zh = "zhBase";
                    String en = "enBase";
                    String outputPath =fixPath(basePath+file.getName().replace(".js","")+".txt");
                    Map<String, JSONObject> baseMap = StringsHelper.getBaseMap(fileStringResult,baseMapKeyList);
                    mapList.add(baseMap);
                    resultAnalyzeHelper.outputFailResult(failResultsOfAllMaps, mapList, file,outputPath,zh,en);

                } else {
                    Map<String, Integer> exportStringsMap = new HashMap();
                    exportStringsMap.put("stringsExport", 0);
                    List stringsList = new ArrayList();
                    List zhHantList = new ArrayList();
                    List<Map<String, JSONObject>> mapList = new ArrayList();
                    String zh = "zh";
                    String en = "en";
                    String outputPath =fixPath(basePath+file.getName().replace(".js","")+".txt");
                    fileNameList.add(file.getName().substring(file.getName().lastIndexOf("-"),file.getName().indexOf(".js")).replace("-",""));

                    //将字符串按照格式，划分为stringsList,zhHantList
                    StringsHelper.convertStringFileToListFile(stringsList, zhHantList, fileStringResult, exportStringsMap);
                    resultAnalyzeHelper.convertStringAndZhhantToMap(zhHantList, stringsList, mapList, exportStringsMap);
                    resultAnalyzeHelper.outputFailResult(failResultsOfAllMaps, mapList, file,outputPath,zh,en);
                }
            }
        }

        //todo 所有结果的处理都是需要等到循环后在做，这个结果怎么来分类处理呢？？？对最终结果进行逐个的判断
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> all all all  all all is finished <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> result path is {} <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<",resultPath);

        BufferedWriter writer  = new BufferedWriter(new FileWriter(resultPath, false));
        //针对多个localizedStrings 下多个文件的（一个文件里面包含多个国家）
        if (failResultsOfAllMaps.size() != 0) {
            for (Map.Entry<File, HashMap<String, JSONObject>> entry : failResultsOfAllMaps.entrySet()) {
                LOGGER.info("file is {},fail result is {}", entry.getKey(), entry.getValue());
                writer.write(entry.getKey()+","+entry.getValue());
                writer.newLine();
                writer.flush();
            }
        //用于不同国家在不同的文件里面
        }else if(totalKeySet.size()!=0){
            writer.write("keys,");
            for(String s:fileNameList){
                writer.write(s+",");
            }
            writer.newLine();
            writer.flush();
            //将所有的key都落盘
            for(String key:totalKeySet){
                writer.write(key+",");
                for(int j=0;j<jsonList.size();j++){
                    JSONObject object = jsonList.get(j);
                    if(!object.isNull(key)){
                        writer.write(object.getString(key)+",");
                    }else {
                        writer.write("N/A,");
                    }
                }
                writer.newLine();
                writer.flush();
            }
        }


        else {
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
