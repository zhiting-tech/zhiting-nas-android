package com.zhiting.networklib.utils.gsonutils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * date : 2021/6/316:28
 * desc :
 */
public class BaseResponseEntityDeserializer<T> implements JsonDeserializer<BaseResponseEntity<T>> {

    @Override
    public BaseResponseEntity<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObj = json.getAsJsonObject();
        final int status = jsonObj.get("status").getAsInt();
        JsonElement msg1 = jsonObj.get("reason");
        JsonElement dataElement = null;
        if (jsonObj.has("data")) {
            dataElement = jsonObj.get("data");
        }
        String msg;
        if (msg1.isJsonNull()) {
            msg = "";
        } else if (msg1.isJsonObject()) {
            msg = msg1.getAsJsonObject().toString();
        } else {
            msg = msg1.getAsString();
        }
        if (dataElement!=null) {
            String str = dataElement.toString();
            if (isEmpty(str)) {
                return new BaseResponseEntity<T>(status, msg, (T) jsonObj.get("data").toString());
            } else if ("class java.lang.String".equals(((ParameterizedType) typeOfT).getActualTypeArguments()[0].toString())) {
                return new BaseResponseEntity<T>(status, msg, (T) jsonObj.get("data").toString());
            } else {
                final T data = context.deserialize(jsonObj.get("data"), ((ParameterizedType) typeOfT).getActualTypeArguments()[0]);
                return new BaseResponseEntity<T>(status, msg, data);
            }
        }else {
            return new BaseResponseEntity<T>(status, msg);
        }
    }

    static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0 || "null".contentEquals(s) || "\"\"".contentEquals(s);
//        return s == null || s.length() == 0 || "null".contentEquals(s) || "\"\"".contentEquals(s) || "[]".contentEquals(s);
    }
}
