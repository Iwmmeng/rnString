package com.xiaomi.jstool;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainEntry.class);

    public static void main(String[] args) throws IOException, JSONException {
//        String filePath="/Users/huamiumiu/Desktop/rn框架/LocalizedStrings";
//        String filePath="/Users/huamiumiu/Desktop/rn框架/LocalizedStrings/AboutPage.js";  //done
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
        String filePath="/Users/huamiumiu/Desktop/rn框架/Other.js";    //todo



//        String  filePath = "/Users/huamiumiu/Desktop/rn框架/MHLocalizableString.js";
//        String  filePath = "/Users/huamiumiu/Desktop/rn框架/pro/Base-EN.js";

        Utils utils = new Utils();
        Map <String,JSONObject> map = new HashMap();
        List list = new ArrayList();
        List fileStringToList = new ArrayList();
        List fileStringToListTmp = new ArrayList();
        List fileList = Utils.getAllDirsAndFiles(list,new File(filePath),"js");
        LOGGER.info("all fileList is:{}",fileList);

        //对每个文件进行处理
        for(int i=0;i<fileList.size();i++) {
            Map <String,JSONObject> stringsMap = new HashMap();
            Map <String,Integer> exportStringsMap = new HashMap();
            Map <String,JSONObject> zhHantMapTmp = new HashMap();
            Map <String,JSONObject> zhHantMap = new HashMap();

            exportStringsMap.put("stringsExport",0);
            List  stringsList = new ArrayList();
            List zhHantList = new ArrayList();
            List foreignList = new ArrayList();
            List fantiCNList = new ArrayList();
            List mapList = new ArrayList();
            //选取其中的一个文件
            LOGGER.info("当前文件为：{}",fileList.get(i).toString());
            //将文件转化为string格式来处理
            String fileStringResult  = Utils.getStringFileFromPath(fileList.get(i).toString());
            //将字符串按照格式，划分为stringsList,zhHantList
           Utils.convertStringFileToListFile(stringsList,zhHantList,fileStringResult,exportStringsMap);
           if(zhHantList.size()==stringsList.size() || stringsList.size()==exportStringsMap.get("stringsExport")){
               for(int t=0;t<stringsList.size();t++) {
//               for(int t=0;t<1;t++) {
                   Map<String, JSONObject> mapTmp = new HashMap();
                   String strTmp = stringsList.get(t).toString();
                   //去掉最外层大括号,首位从第一位开始
                   String str = strTmp.substring(1, strTmp.lastIndexOf("}")).trim();
                   Utils.parseStringToList(str, foreignList, fantiCNList);

                   zhHantMap = utils.parseHantStringToMap(zhHantList, zhHantMapTmp);
                   for(Map.Entry entry:zhHantMap.entrySet()){
                       System.out.println("=========================");
                       System.out.println("key is "+entry.getKey()+"value is "+entry.getValue());
                   }
                   map = utils.parseStringsToMap(foreignList, fantiCNList, mapTmp, zhHantMap);
                   for(String key:map.keySet()){
                       System.out.println("key: "+key+"value:"+map.get(key));
                   }
                   mapList.add(map);
               }

           }else {
               LOGGER.error("文件的格式不对称");
           }
        }


        System.out.println(list);
    }









//        String string2 = sb.substring(sb.indexOf("const"),sb.indexOf(";")).trim();
//        String[] strings= string2.split("=");
//        JSONObject jsonObject = new JSONObject(string4Map);
//        JSONArray jsonArray = new JSONArray(string1);
//        jsonObject.put(strings[0],strings[1]);
//        System.out.println(jsonObject);




    @Test
    public void test() {
        String str = "//英文\t\tproduct_name";
        String[] arr = str.split("\t");
        System.out.println(arr.length);
        System.out.println(arr[0]);
        System.out.println(arr[1]);
    }
    @Test
    public void test01(){
        String s1= "01234567";
        String s2 ="12";
//        StringUtils.remove(s1,s2);
        System.out.println(StringUtils.remove(s1,s2));
    }


}
