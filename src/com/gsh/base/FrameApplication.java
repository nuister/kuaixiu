package com.gsh.base;

import android.content.Context;
import android.content.SharedPreferences;

import com.litesuits.ApplicationBase;

import cn.sharesdk.framework.ShareSDK;

/**
 * @author Tan Chunmao
 */
public class FrameApplication extends ApplicationBase {
    /*molin release test*/
    //打包前设置成false
    public static final boolean test = true;
    public static Context context;
    public static SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
//        initUser();
        ShareSDK.initSDK(getApplicationContext());//initialize share sdk
    }
}
