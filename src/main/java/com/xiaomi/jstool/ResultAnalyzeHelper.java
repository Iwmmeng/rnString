package com.xiaomi.jstool;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ResultAnalyzeHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultAnalyzeHelper.class);


    //主要是给文件夹localizedStrings用的
    public void outputFailResult(Map<File, HashMap<String, JSONObject>> resultMap, List<Map<String, JSONObject>> mapList, File file) throws JSONException {
        ResultAnalyzeHelper analyzeHelper = new ResultAnalyzeHelper();
        Map<String, JSONObject> failResultMap = analyzeHelper.getFailResultMap(mapList);
        if (failResultMap.size() != 0) {
            resultMap.put(file, (HashMap<String, JSONObject>) failResultMap);
        } else {
            LOGGER.info("failResultMap is null,good job");
        }
    }


    private Map getFailResultMap(List<Map<String, JSONObject>> mapList) throws JSONException {
        Map<String, JSONObject> failResultMap = null;
        if (mapList.size() != 0) {
            for (int mapNum = 0; mapNum < mapList.size(); mapNum++) {
                failResultMap = new HashMap<String, JSONObject>();
                LOGGER.info("###################### 第 {} 个map,total is {} #####################", mapNum + 1, mapList.size());
                LOGGER.info("mapList.get(mapNum) is {}", mapList.get(mapNum));
                ResultAnalyzeHelper resultAnalyzeHelper = new ResultAnalyzeHelper();
                resultAnalyzeHelper.analyseMapResult(mapList.get(mapNum), failResultMap);
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

    //主要用于LocalizedStrings，一个文件里面含有
    private void analyseMapResult(Map<String, JSONObject> map, Map<String, JSONObject> resultMap) throws JSONException {
        List mapKeyList = new ArrayList();
        //提取map的key，作为索引
        for (String key : map.keySet()) {
            mapKeyList.add(key);
        }
        LOGGER.info("mapKeyList size is {},list is {}", mapKeyList.size(), mapKeyList);
        //校验各个JSONObject的长度都是一致的  done
        //提取一组JSONObject的key
        List jsonKeyList = new ArrayList();
        JSONObject jsonObject = map.get(mapKeyList.get(0));
        Iterator iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String jsonKey = (String) iterator.next();
            jsonKeyList.add(jsonKey);
        }
        LOGGER.info("jsonKeyList size is {},list is {}", jsonKeyList.size(), jsonKeyList);
        //对整体按照提取粗来的mapKey和jsonKey 来获取记录的值，表达出来

//        Map<String, JSONObject> failResult = new HashMap<String, JSONObject>();
        int ZH = mapKeyList.indexOf("zh");
        int EN = mapKeyList.indexOf("en");
        LOGGER.info("ZH is {},EN is {}", ZH, EN);
        for (int m = 0; m < jsonKeyList.size(); m++) {
            LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>start to parse 第 {} 个 jsonKey，total is {} ,jsonKey is {}", m + 1, jsonKeyList.size(), jsonKeyList.get(m));
            List jsonValueList = new ArrayList();
            for (int n = 0; n < mapKeyList.size(); n++) {
                String jsonValue = map.get(mapKeyList.get(n)).getString(jsonKeyList.get(m).toString());
                jsonValueList.add(jsonValue);
            }
            LOGGER.info("jsonValueList size {},list is {}", jsonValueList.size(), jsonValueList);
            //对jsonValueList进行遍历，判断标准：跟zh和en相同的即为不符合要求的
            for (int t = 0; t < jsonValueList.size(); t++) {
                if (t == ZH || t == EN) {
                    continue;
                } else {
                    if (jsonValueList.get(t).equals(jsonValueList.get(ZH)) || jsonValueList.get(t).equals(jsonValueList.get(EN))) {
                        JSONObject failJsonObject = new JSONObject();
                        failJsonObject.put(jsonKeyList.get(m).toString(), jsonValueList.get(t).toString());
                        resultMap.put(mapKeyList.get(t).toString(), failJsonObject);
                    } else {
                        LOGGER.info("good job,passed");
                    }
                }
            }
            LOGGER.info("第 {} 个 jsonKeyList 的遍历结束，总共需要遍历{}. 此次 fail 的结果为 {}", m + 1,jsonKeyList.size(), resultMap);
        }
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>.parse finish <<<<<<<<<<<<<<<<<<<<<");
        if (resultMap.size() != 0) {
            for (Map.Entry<String, JSONObject> entry : resultMap.entrySet()) {
                LOGGER.info("fail result key is {}, value is {}", entry.getKey(), entry.getValue());
            }
        } else {
            LOGGER.info("great,great,great,great,all are passed");
        }
    }


}




