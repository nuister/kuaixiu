package com.gsh.kuaixiu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.gsh.kuaixiu.activity.OrderDetailNewActivity;
import com.litesuits.common.cache.XmlCache;

/**
 *@author Tan Chunmao
 */
public class TcpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Constant.Action.ORDER)) {
            boolean active = XmlCache.getInstance().getBoolean(OrderDetailNewActivity.class.getName());
            if (!active) {

            }
        }
    }
}
