package com.gsh.base.https;

import com.google.gson.JsonElement;
import com.gsh.kuaixiu.Constant;
import com.litesuits.http.data.FastJSonImpl;
import com.litesuits.http.data.Json;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by taosj on 15/1/30.
 */
public final class ApiResult implements Serializable {

    private static Json json = new FastJSonImpl();

    public ApiResult() {
    }

    public JsonElement data;
    public String message;
    public int code;

    public <T> T getModel(Class<T> clazz) {
        return new FastJSonImpl().toObject(data.toString(), clazz);
    }

    public <T> List<T> getModels(Class<T> clazz) {
        return json.toObjects(data.toString(), clazz);
    }

    public <T> T getModel(Class<T> clazz, JSONMapper mapper) {
        return new FastJSonImpl().toObject(mapper.build(data).toString(), clazz);
    }

    public <T> List<T> getModels(Class<T> clazz, JSONMapper mapper) {
        return json.toObjects(mapper.build(data).toString(), clazz);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [result="
                + ("[message="
                + message + ",code=" + code + "]" + "]");
    }

    public boolean isOk() {
        return code == Constant.Urls.CODE_SUCCESS;
    }

    public static class JSONMapper {

        public HashMap<String, String> map = new HashMap<String,String>();

        public JSONMapper map(String key, String newKey) {
            map.put(key, newKey);
            return this;
        }

        public JsonElement build(JsonElement json) {
            for (String key : map.keySet()) {
//                json.replace(key + ":", map.get(key) + ":");
            }
            return null;
        }
    }
}

