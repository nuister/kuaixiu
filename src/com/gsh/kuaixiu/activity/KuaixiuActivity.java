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

import android.content.Intent;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;
import com.gsh.kuaixiu.R;
import com.gsh.kuaixiu.SocketServer;
import com.gsh.kuaixiu.activity.fragment.KuaixiuFragment;
import com.gsh.kuaixiu.activity.fragment.KuaixiuMenuFragment;
import com.litesuits.android.log.Log;
import com.litesuits.android.widget.sidemenu.SlidingMenu;
import com.litesuits.android.widget.sidemenu.app.SlidingActivity;

/**
 * @author Tan Chunmao
 */
public class KuaixiuActivity extends SlidingActivity {

    private SlidingMenu slidingMenu;
    private LocationListener locationListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sliding);
        initSlidingMenu();


        Intent intent = new Intent(this, SocketServer.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initSlidingMenu() {
        slidingMenu = super.getSlidingMenu();
        if (slidingMenu == null) {
            Log.d("test", "baga");
        }

        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.sliding_shadow);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);//设置侧边栏右边距屏幕右边宽度
        slidingMenu.setFadeDegree(0.35f);

        setSlidingActionBarEnabled(true);

        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        // set the Behind View
        setBehindContentView(R.layout.fragment_menu_frame);
        this.getFragmentManager().beginTransaction().replace(R.id.menu_frame, new KuaixiuMenuFragment()).commit();

        setContentView(R.layout.fragment_content_frame);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new KuaixiuFragment()).commit();
    }


    public SlidingMenu getSlidingMenu() {
        return slidingMenu;
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
       /* if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (!slidingMenu.isMenuShowing()) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    toast("再按一次 退出程序");
                    exitTime = System.currentTimeMillis();
                } else {
                    finishAll();
                }
                return true;
            }
        }*/
        return super.onKeyDown(keyCode, event);
    }


    public void toast(int s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
