package com.litesuits.android.polling;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.gsh.kuaixiu.Constant;
import com.gsh.kuaixiu.R;
import com.litesuits.android.log.Log;

/**
 * Created by admin on 6/3/15.
 */
public class PollingService extends Service {

    public static final String ACTION = "com.ryantang.service.PollingService";

    private Notification mNotification;
    private NotificationManager mManager;


    @Override
    public void onCreate() {
//        initNotifiManager();
        Log.d("test","polling service on create");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Intent broadIntent = new Intent(Constant.Action.POLLING);
        sendBroadcast(broadIntent);
        Log.d("test","polling service on start");
    }

    //初始化通知栏配置
    private void initNotifiManager() {
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int icon = R.drawable.ic_launcher;
        mNotification = new Notification();
        mNotification.icon = icon;
        mNotification.tickerText = "New Message";
        mNotification.defaults |= Notification.DEFAULT_SOUND;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
    }

    //弹出Notification
    private void showNotification() {
//        mNotification.when = System.currentTimeMillis();
//        //Navigator to the new activity when click the notification title
//        Intent i = new Intent(this, MessageActivity.class);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
//                Intent.FLAG_ACTIVITY_NEW_TASK);
//        mNotification.setLatestEventInfo(this,getResources().getString(R.string.app_name), "You have new message!", pendingIntent);
//        mManager.notify(0, mNotification);
    }

    /**
     * Polling thread
     * 模拟向Server轮询的异步线程
     *
     * @Author Ryan
     * @Create 2013-7-13 上午10:18:34
     */
    int count = 0;

    class PollingThread extends Thread {
        @Override
        public void run() {
            System.out.println("Polling...");
            count++;
            //当计数能被5整除时弹出通知
            if (count % 5 == 0) {
                showNotification();
                System.out.println("New message!");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("test","polling service on destroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
