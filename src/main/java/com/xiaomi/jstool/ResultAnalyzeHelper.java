package com.xiaomi.jstool;

import org.json.JSONException;
import org.json.JSONObject;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ResultAnalyzeHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultAnalyzeHelper.class);


    //主要是给文件夹localizedStrings用的
    public void outputFailResult(Map<File, HashMap<String, JSONObject>> failResultsOfAllMaps, List<Map<String, JSONObject>> mapList, File file, String allOutputPath, String zh, String en) throws JSONException, IOException {
        ResultAnalyzeHelper analyzeHelper = new ResultAnalyzeHelper();
        Map<String, JSONObject> failResultMap = analyzeHelper.getFailResult(mapList, allOutputPath, zh, en);
        if (failResultMap.size() != 0) {
            failResultsOfAllMaps.put(file, (HashMap<String, JSONObject>) failResultMap);
        } else {
            LOGGER.info("failResultMap is null,no need to put in failResultsOfAllMaps ");
        }
    }

    //获取结果的入口
    private Map getFailResult(List<Map<String, JSONObject>> mapList, String allOutputPath, String zh, String en) throws JSONException, IOException {
        Map<String, JSONObject> failResultMap = null;
        if (mapList.size() != 0) {
            for (int mapNum = 0; mapNum < mapList.size(); mapNum++) {
                failResultMap = new HashMap<String, JSONObject>();
                LOGGER.info("###################### 第 {} 个map,total is {} #####################", mapNum + 1, mapList.size());
                LOGGER.info("mapList.get(mapNum) is {}", mapList.get(mapNum));
                ResultAnalyzeHelper resultAnalyzeHelper = new ResultAnalyzeHelper();
                resultAnalyzeHelper.analyseMapResult(mapList.get(mapNum), failResultMap, allOutputPath, zh, en);
            }
        } else {
            LOGGER.info("mapList is null");
        }
        return failResultMap;
    }

    public void convertStringAndZhhantToMap(List zhHantList, List stringsList, List mapList, Map<String, Integer> exportStringsMap) throws JSONException {
        if (zhHantList.size() == stringsList.size() && stringsList.size() == exportStringsMap.get("stringsExport")) {
            for (int t = 0; t < stringsList.size(); t++) {
                Map<String, JSONObject> stringsMap = new HashMap();
                String strSub = stringsList.get(t).toString();
                String zhHant = zhHantList.get(t).toString();
                List foreignList = StringsHelper.parseStringToList(strSub);
                StringsHelper.parseStringsToMap(foreignList, stringsMap, zhHant);
                mapList.add(stringsMap);
            }
        } else {
            LOGGER.error("文件的格式不对称");
        }
    }

    //主要用于LocalizedStrings，传入一个正常的map，输出fail的map
    private void analyseMapResult(Map<String, JSONObject> map, Map<String, JSONObject> failResultMap, String allOutputPath, String zh, String en) throws JSONException, IOException {
        List mapKeyList = new ArrayList();
        //提取map的key，作为索引
        for (String key : map.keySet()) {
            mapKeyList.add(key);
        }
        LOGGER.info("mapKeyList size is {},list is {}", mapKeyList.size(), mapKeyList);
        //校验各个JSONObject的长度都是一致的  done
        //提取一组JSONObject的key
        List jsonKeyList = new ArrayList();
        List<List<String>> jsonValueListOutput;
        JSONObject jsonObject = map.get(mapKeyList.get(0));
        Iterator iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String jsonKey = (String) iterator.next();
            jsonKeyList.add(jsonKey);
        }
        LOGGER.info("jsonKeyList size is {},list is {}", jsonKeyList.size(), jsonKeyList);
        //对整体按照提取粗来的mapKey和jsonKey 来获取记录的值，表达出来
        jsonValueListOutput = getJsonValueList(jsonKeyList, map, mapKeyList, allOutputPath);

        int ZH = mapKeyList.indexOf(zh);
        int EN = mapKeyList.indexOf(en);
        LOGGER.info("ZH is {},EN is {}", ZH, EN);
        getFailResultMap(jsonValueListOutput, ZH, EN, mapKeyList, failResultMap);
    }

    public void getFailResultMap(List<List<String>> jsonValueListOutput, int ZH, int EN, List mapKeyList, Map<String, JSONObject> failResultMap) throws JSONException {
//        Map<String, JSONObject> failResultMap = new HashMap<String, JSONObject>();
        for (int nc = 0; nc < jsonValueListOutput.size(); nc++) {
            LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>start to parse 第 {} 个 jsonKey，total is {}，jsonKey is {} ", nc + 1, jsonValueListOutput.size(), jsonValueListOutput.get(nc).get(0).toString());
            List jsonValueList = jsonValueListOutput.get(nc);
            //需要从第一位开始，第0位是jsonKey
            for (int jc = 1; jc < jsonValueList.size(); jc++) {
                if (jc == ZH || jc == EN) {
                    continue;
                } else {
                    if (jsonValueList.get(jc).equals(jsonValueList.get(ZH)) || jsonValueList.get(jc).equals(jsonValueList.get(EN))) {
                        JSONObject failJsonObject = new JSONObject();
                        failJsonObject.put(jsonValueList.get(0).toString(), jsonValueList.get(jc).toString());
                        //mapKeyList 比jsonValueList size少1
                        failResultMap.put(mapKeyList.get(jc - 1).toString(), failJsonObject);
                    } else {
                        LOGGER.info("good job,passed");
                    }
                }
            }
        }
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>> parse current file finish,fail 的结果为 {}<<<<<<<<<<<<<<<<<<<<<", failResultMap);
    }


    /**
     * jsonValueList:[jsonKey,jsonValue1,jsonValue12]
     */
    public List getJsonValueList(List jsonKeyList, Map<String, JSONObject> map, List mapKeyList, String allOutputPath) throws JSONException, IOException {
        List<List<String>> jsonValueListOutput = new ArrayList<List<String>>();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(allOutputPath,true),"UTF-8");
        BufferedWriter writer = new BufferedWriter(outputStreamWriter);
//        BufferedWriter writer = new BufferedWriter(new FileWriter(allOutputPath, true));
        writer.write("name,");
        for (int n = 0; n < mapKeyList.size(); n++) {
            writer.write(mapKeyList.get(n).toString() + ",");
        }
        writer.newLine();

        for (int m = 0; m < jsonKeyList.size(); m++) {
            List jsonValueList = new ArrayList();
            //TODO 对当前整个文件的数据落盘输出
            writer.write(jsonKeyList.get(m).toString() + ",");
            for (int n = 0; n < mapKeyList.size(); n++) {
                String jsonValue = map.get(mapKeyList.get(n)).getString(jsonKeyList.get(m).toString());
                jsonValueList.add(jsonValue + ",");
                writer.write(jsonValue);
            }
            writer.newLine();
            writer.flush();
            //把jsonKey给设置进去，方便输出到文件
            jsonValueList.add(0, jsonKeyList.get(m));
            jsonValueListOutput.add(jsonValueList);
            LOGGER.info("jsonValueList size {},list is {}", jsonValueList.size(), jsonValueList);
        }
        return jsonValueListOutput;
    }






}




