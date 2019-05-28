package com.xiaomi.jstool;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ResultAnalyze {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultAnalyze.class);
    public void analyseMapResult(Map<String, JSONObject> map,Map<String, JSONObject> resultMap) throws JSONException {
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
        LOGGER.info("ZH is {},EN is {}",ZH,EN);
        for (int m = 0; m < jsonKeyList.size(); m++) {
            LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>start to parse 第 {} 个jsonKey，total is {} ,jsonKey is {}", m+1, jsonKeyList.size(),jsonKeyList.get(m));
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
            LOGGER.info("第 {} 次遍历结束，结果为 {}",m+1, resultMap);
        }
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>.parse finish <<<<<<<<<<<<<<<<<<<<<");
        if(resultMap.size()!=0){
        for(Map.Entry<String,JSONObject> entry:resultMap.entrySet()){
            LOGGER.info("fail result key is {}, value is {}",entry.getKey(),entry.getValue());
        }
        }else{
            LOGGER.info("great,great,great,great,all are passed");
        }
    }


}




