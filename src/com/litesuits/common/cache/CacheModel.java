package com.litesuits.common.cache;

import java.io.Serializable;

/**
 * Created by taosj on 15/3/19.
 */
public abstract class CacheModel implements Serializable {

    public abstract <T> void save();

    public static   <T> T load(Class<T> tClass){
        return null;
    }
}
