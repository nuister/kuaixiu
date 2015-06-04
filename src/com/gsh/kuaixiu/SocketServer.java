package com.gsh.kuaixiu;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import com.litesuits.android.async.AsyncTask;
import com.litesuits.android.async.TaskExecutor;
import com.litesuits.android.log.Log;
import com.litesuits.socket.TcpClient;
import com.litesuits.socket.model.LogoutRequest;
import com.litesuits.socket.model.Ping;

import java.util.Timer;


/**
 * Activity.
 * <p/>
 *
 * @author <a href="shijun.tao@gangsh.com">Shijun Tao</a>
 *         15/5/29
 */
public class SocketServer extends Service {

    private Object lock = new Object();

    private ConnectivityManager connectivityManager;
    private NetworkInfo info;

    private Timer timer;


    @Override
    public void onCreate() {
        super.onCreate();
        TcpClient.instance.setContext(this);
        TcpClient.instance.connect(Constant.Urls.TCP_HOST, Constant.Urls.TCP_PORT);

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);

        timer = TaskExecutor.startTimerTask(new Runnable() {
            @Override
            public void run() {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("confirm", "confirm");
                        try {
                            TcpClient.instance.sendMsg(new Ping().build());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 5, 10l * 1000l);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (timer != null)
            timer.cancel();
        timer = null;

        LogoutRequest request = new LogoutRequest();
        request.token = User.load(User.class).getToken();
        try {
            TcpClient.instance.sendMsg(request.build());
        } catch (Exception e) {
            e.printStackTrace();
        }

        unregisterReceiver(mReceiver);
        TcpClient.instance.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                connectivityManager = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                TcpClient.instance.sendMsg(new Ping().build());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    };


}
