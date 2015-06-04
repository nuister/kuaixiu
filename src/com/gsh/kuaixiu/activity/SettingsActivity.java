package com.gsh.kuaixiu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.gsh.kuaixiu.User;
import com.gsh.kuaixiu.R;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.common.cache.XmlCache;

/**
 * @author Tan Chunmao
 */
public class SettingsActivity extends KuaixiuActivityBase {
    @InjectView
    private View layout_account, layout_clear_cache, layout_about, next, account_divider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Injector.self.inject(this);
        setTitleBar("设置");
        layout_account.setOnClickListener(onClickListener);
        layout_clear_cache.setOnClickListener(onClickListener);
        layout_about.setOnClickListener(onClickListener);
        next.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(layout_account)) {
                account();
            } else if (v.equals(layout_clear_cache)) {
                notice(new CallBack() {
                    @Override
                    public void call() {
                        clearCache();
                    }
                }, "确定清除缓存");
            } else if (v.equals(layout_about)) {
                about();
            } else if (v.equals(next)) {
                next();
            }
        }
    };

    private void account() {
//        go.name(SettingsAccountActivity.class).go();
    }

    private void clearCache() {

    }

    private void about() {
        go.name(WebActivity.class).param("title", "关于人民优购").param("html", "ugou.html").go();
    }


    private void next() {
        notice(new CallBack() {
            @Override
            public void call() {
                User user = User.load(User.class);
                user.reset();
                user.save();
                XmlCache.getInstance().removeAll();
                final Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                }, 100);
            }
        }, "确定退出？");
    }
}
