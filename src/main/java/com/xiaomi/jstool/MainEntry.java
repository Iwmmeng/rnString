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
        List list = new ArrayList();
        getSubString(string1,list);
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
