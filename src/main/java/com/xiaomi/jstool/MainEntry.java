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

        List list = new ArrayList();
        List fileStringToList = new ArrayList();
        List fileStringToListTmp = new ArrayList();
        List fileList = Utils.getAllDirsAndFiles(list,new File(filePath),"js");
        LOGGER.info("all fileList is:{}",fileList);
        //对每个文件进行处理
        for(int i=0;i<fileList.size();i++) {
            Map <String,JSONObject> map = new HashMap();
            LOGGER.info("current fileList is当前文件路径：{}",fileList.get(i).toString());
            String file = Utils.getStringFileFromPath(fileList.get(i).toString());
            fileStringToList = Utils.convertStringFileToListFile(fileStringToListTmp,file);
            LOGGER.info("fileStringToList is:",fileStringToList);
            Utils.parseFileList(fileStringToList);

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
        System.out.println(StringUtils.remove("abcdef","abc"));
    }


}
