/*
 * Copyright (c) 2014 Gangshanghua Information Technologies Ltd.
 * http://www.gangsh.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Gangshanghua Information Technologies ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you
 * entered into with Gangshanghua Information Technologies.
 */

package com.gsh.kuaixiu.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import com.gsh.kuaixiu.Constant;
import com.gsh.kuaixiu.R;
import com.litesuits.android.log.Log;
import com.litesuits.android.polling.PollingService;
import com.litesuits.android.polling.PollingUtils;

/**
 * @author Tan Chunmao
 */
public class TestActivity extends KuaixiuActivityBase {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
        findViewById(R.id.click_start).setOnClickListener(onClickListener);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.Action.POLLING);
        registerReceiver(broadcastReceiver,intentFilter);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(TextUtils.equals(action, Constant.Action.POLLING)) {
                String message=System.currentTimeMillis()+"_news";
                Log.d("test",message);
                toast(message);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (R.id.click_start == view.getId()) {
                if (view.getTag() == null) {
                    PollingUtils.startPollingService(context, 5, PollingService.class, PollingService.ACTION);
                    setText(R.id.click_start,"stop");
                    view.setTag("");
                } else {
                    PollingUtils.stopPollingService(context, PollingService.class, PollingService.ACTION);
                    setText(R.id.click_start,"start");
                    view.setTag(null);
                }
            }
        }
    };
}
