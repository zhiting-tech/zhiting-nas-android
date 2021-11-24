package com.zhiting.networklib.utils.gsonutils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhiting.networklib.base.mvp.BaseResponseEntity;

public class GsonConverter<T> {

    private static Gson mGson;

    public static <T> Gson getGson() {
        if (null == mGson) {
            synchronized (GsonConverter.class) {
                if (null == mGson) {
                    mGson = new GsonBuilder()
                            .registerTypeAdapter(String.class, new StringTypeAdapter())
                            .registerTypeAdapter(Integer.class, new IntegerDeserializer())
                            .registerTypeAdapter(Double.class, new DoubleDeserializer())
                            .registerTypeAdapter(Long.class, new LongDeserializer())
                            .registerTypeAdapter(BaseResponseEntity.class, new BaseResponseEntityDeserializer<T>())
                            .create();
                }
            }
        }
        return mGson;
    }
}
