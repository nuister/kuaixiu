package com.gsh.kuaixiu.activity;

import android.os.Bundle;
import android.os.Handler;
import com.gsh.base.activity.MainActivity;
import com.gsh.kuaixiu.R;
import com.gsh.kuaixiu.User;

/**
 * @author Tan Chunmao
 */
public class SplashActivity extends KuaixiuActivityBase {
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                entry();
            }
        }, 3000);

    }

    private void entry() {
        User user = User.load(User.class);
        if (user.loggedIn())
            go.name(KuaixiuActivity.class).goAndFinishCurrent();
        else
            go.name(LoginActivity.class).goAndFinishCurrent();
//        go.name(KuaixiuActivity.class).goAndFinishCurrent();
    }
}
