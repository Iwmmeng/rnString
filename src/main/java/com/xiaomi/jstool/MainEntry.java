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

    public static void main(String[] args) throws IOException, JSONException {
//        String filePath="/Users/huamiumiu/Desktop/rn框架/LocalizedStrings";
//        String filePath = "/Users/huamiumiu/Desktop/rn框架/LocalizedStrings/AboutPage.js";  //done
//        String filePath="/Users/huamiumiu/Desktop/rn框架/LocalizedStrings/BeginnerGuidePage.js";  //done
//        String filePath="/Users/huamiumiu/Desktop/rn框架/LocalizedStrings/BrushHeadDetailPage.js";  //done
//        String filePath="/Users/huamiumiu/Desktop/rn框架/LocalizedStrings/DFUPage.js";  //done
//        String filePath="/Users/huamiumiu/Desktop/rn框架/LocalizedStrings/HealthKnowledgePage.js";  //done
//        String filePath="/Users/huamiumiu/Desktop/rn框架/LocalizedStrings/HistoryPage.js";  //done
//        String filePath="/Users/huamiumiu/Desktop/rn框架/LocalizedStrings/HistoryReportPage.js";  //done
//        String filePath="/Users/huamiumiu/Desktop/rn框架/LocalizedStrings/HomePage.js";  //done
//        String filePath="/Users/huamiumiu/Desktop/rn框架/LocalizedStrings/IntroducePlaquePage.js";  //done
//        String filePath="/Users/huamiumiu/Desktop/rn框架/LocalizedStrings/LastBrushDataDeatilPage.js";  //done
//        String filePath="/Users/huamiumiu/Desktop/rn框架/LocalizedStrings/PersonalSettingPage.js";  //done
//        String filePath="/Users/huamiumiu/Desktop/rn框架/LocalizedStrings/QuickGuidPage.js";  //done
        //todo 这个文件的格式获取到的结果有问题
//        String filePath="/Users/huamiumiu/Desktop/rn框架/LocalizedStrings/SettingPage.js";  // todo
//        String filePath="/Users/huamiumiu/Desktop/rn框架/LocalizedStrings/WhyReplaceBrushheadPage.js";  //done
        // todo 文件获取的格式有问题
//        String filePath="/Users/huamiumiu/Desktop/rn框架/problem/Other.js";    //todo
//        String filePath = "/Users/huamiumiu/Desktop/rn框架/problem/SettingPage.js";    //todo
        String filePath = "/Users/huamiumiu/Desktop/rn框架/problem";    //todo


//        String  filePath = "/Users/huamiumiu/Desktop/rn框架/MHLocalizableString.js";
//        String  filePath = "/Users/huamiumiu/Desktop/rn框架/pro/Base-EN.js";

        List list = new ArrayList();
        List<File> fileList = Utils.getAllDirsAndFiles(list, new File(filePath), "js");
        LOGGER.info("all fileList is:{}", fileList);
        //对每个文件进行处理
        for (int i = 0; i < fileList.size(); i++) {
            LOGGER.info("对第 {} 个文件进行处理，total is {}", i + 1, fileList.size());
            Utils utils = new Utils();
            Map<String, JSONObject> map = new HashMap();
            Map<String, JSONObject> stringsMap = new HashMap();
            Map<String, Integer> exportStringsMap = new HashMap();
            Map<String, JSONObject> zhHantMapTmp = new HashMap();
            Map<String, JSONObject> zhHantMap = new HashMap();

            exportStringsMap.put("stringsExport", 0);
            List stringsList = new ArrayList();
            List zhHantList = new ArrayList();
            List foreignList = new ArrayList();
            List fantiCNList = new ArrayList();
            List<Map<String, JSONObject>> mapList = new ArrayList();
            //选取其中的一个文件
            LOGGER.info("当前文件为：{}", fileList.get(i).toString());
            //将文件转化为string格式来处理
            String fileStringResult = Utils.getStringFileFromPath(fileList.get(i).toString());
            //将字符串按照格式，划分为stringsList,zhHantList
            Utils.convertStringFileToListFile(stringsList, zhHantList, fileStringResult, exportStringsMap);
            if (zhHantList.size() == stringsList.size() || stringsList.size() == exportStringsMap.get("stringsExport")) {
                for (int t = 0; t < stringsList.size(); t++) {
//               for(int t=0;t<1;t++) {
                    Map<String, JSONObject> mapTmp = new HashMap();
                    String strTmp = stringsList.get(t).toString();
                    //去掉最外层大括号,首位从第一位开始
                    String str = strTmp.substring(1, strTmp.lastIndexOf("}")).trim();
                    utils.parseStringToList(str, foreignList, fantiCNList);
                    //把zhHantList转换为zhHantMap<String, JSONObject>
                    zhHantMap = utils.parseHantStringToMap(zhHantList, zhHantMapTmp);
                    for (Map.Entry entry : zhHantMap.entrySet()) {
                        System.out.println("=========================");
                        System.out.println("key is " + entry.getKey() + "value is " + entry.getValue());
                    }
                    //对String的格式做处理，且将zhHantMap<String, JSONObject>格式统统存到mp里面去
                    map = utils.parseStringsToMap(foreignList, fantiCNList, mapTmp, zhHantMap);
//                    for (String key : map.keySet()) {
//                        System.out.println("key: " + key + "value:" + map.get(key));
//                    }
                    mapList.add(map);
                }
//                System.out.println("》》》》》》》》mapList" + mapList);
//                for(int k=0;k<mapList.size();k++){
//                    System.out.println("===="+mapList.get(k));
//                }
            } else {
                LOGGER.error("文件的格式不对称");
            }
            for (int mapNum = 0; mapNum < mapList.size(); mapNum++) {
                Map<String, JSONObject> failResultMap = new HashMap<String, JSONObject>();
                LOGGER.info("list file is {}", list);
                LOGGER.info("第 {} 个map,total is {}", mapNum + 1, mapList.size());
                LOGGER.info("mapList.get(mapNum) is {}", mapList.get(mapNum));
                ResultAnalyze resultAnalyze = new ResultAnalyze();
                resultAnalyze.analyseMapResult(mapList.get(mapNum), failResultMap);
                if(failResultMap.size()!=0){
                    resultMap.put(fileList.get(i), (HashMap<String, JSONObject>) failResultMap);
                }else {
                    LOGGER.info("failResultMap is null");
                }
            }
        }
        LOGGER.info("all is finished <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        if (resultMap.size()!= 0) {
            for (Map.Entry<File, HashMap<String, JSONObject>> entry : resultMap.entrySet()) {
//                for (Map.Entry<String, JSONObject> subEntry : entry.getValue().entrySet()) {
                    LOGGER.info("file is {},fail result is {}", entry.getKey(), entry.getValue());
//                }
            }
        } else {
            LOGGER.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< all is passed,congratulations!!!!! ");
        }
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
