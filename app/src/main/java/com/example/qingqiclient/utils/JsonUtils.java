package com.example.qingqiclient.utils;

import com.example.qingqiclient.entity.EI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/17 0017.
 * 主要用于json数据的解析
 * 这里我们使用了谷歌提供的GSON开源库
 */

public class JsonUtils {

    /**
     * 这个方法用来解析一段与EI类对应的json数组
     * 如果数组为空，我是说如果，那么返回一个空数组
     * @param jsonData
     * @return
     */
    public static List<EI> parseEIListWithGSON(String jsonData){
        List<EI> eiList = new ArrayList<>();
        Gson gson = new Gson();
        eiList = gson.fromJson(jsonData, new TypeToken<List<EI>>(){}.getType());
        return eiList;
    }
}
