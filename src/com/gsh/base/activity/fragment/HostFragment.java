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

package com.gsh.base.activity.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.gsh.kuaixiu.R;
import com.gsh.base.activity.MainActivity;

public class HostFragment extends FragmentBase {
    public static String curFragmentTag;
    private FragmentManager fragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Tab currentTab;


    @Override
    public void refresh() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null)
                parent.removeView(layout);
            return layout;
        }
        layout = inflater.inflate(R.layout.fragment_host, container, false);
        findViewById(R.id.menu).setOnClickListener(onClickListener);

        //test
        initTabs();
        fragmentManager = activity.getFragmentManager();
        switchFragment(Tab.main);
        //test
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void initTabs() {
        for (Tab tab : Tab.values()) {
            final View container = findViewById(tab.containerId);
            container.setOnClickListener(onClickListener);
            container.setTag(tab);
            ((ImageView) container.findViewById(R.id.icon)).setImageResource(tab.iconRid);
            ((TextView) container.findViewById(R.id.label)).setText(tab.labelText);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (R.id.menu == v.getId()) {
                ((MainActivity) activity).getSlidingMenu().showMenu();
            } else {
                Tab selectedTab = (Tab) v.getTag();
                if (currentTab != selectedTab)
                    switchFragment(selectedTab);
            }
        }
    };

    public void switchFragment(Tab selectedTab) {
        currentTab = selectedTab;

        for (Tab tab : Tab.values()) {
            View view = findViewById(tab.containerId);
            boolean isSelected = (tab == selectedTab);
            view.setSelected(isSelected);
        }
        String tag = selectedTab.name();
        if (TextUtils.equals(tag, curFragmentTag)) {
            return;
        }
        if (curFragmentTag != null) {
            detachFragment(getFragment(curFragmentTag));
        }
        ((TextView)findViewById(R.id.title)).setText(selectedTab.labelText);
        FragmentBase currentFragment = (FragmentBase) getFragment(tag);
        attachFragment(R.id.host_content, currentFragment, tag);
        curFragmentTag = tag;
        commitTransactions();
        if (currentFragment.getActivity() != null)
            currentFragment.refresh();
    }

    private void detachFragment(Fragment f) {

        if (f != null && !f.isDetached()) {
            ensureTransaction();
            mFragmentTransaction.detach(f);
        }
    }

    private FragmentTransaction ensureTransaction() {
        if (mFragmentTransaction == null) {
            mFragmentTransaction = fragmentManager.beginTransaction();
            mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }
        return mFragmentTransaction;
    }

    private void attachFragment(int layout, Fragment f, String tag) {
        if (f != null) {
            if (f.isDetached()) {
                ensureTransaction();
                mFragmentTransaction.attach(f);
            } else if (!f.isAdded()) {
                ensureTransaction();
                mFragmentTransaction.add(layout, f, tag);
            }
        }
    }

    private void commitTransactions() {
        if (mFragmentTransaction != null && !mFragmentTransaction.isEmpty()) {
            mFragmentTransaction.commitAllowingStateLoss();
            mFragmentTransaction = null;
        }
    }

    private Fragment getFragment(String tag) {
        Fragment f = fragmentManager.findFragmentByTag(tag);
        if (f == null) {
            if (TextUtils.equals(tag, Tab.main.name())) {
                f = new FirstFragment();
            } else if (TextUtils.equals(tag, Tab.category.name())) {
                f = new SecondFragment();
            } else if (TextUtils.equals(tag, Tab.cart.name())) {
                f = new ThirdFragment();
            } else if (TextUtils.equals(tag, Tab.profile.name())) {
                f = new FourthFragment();
            }
        }
        return f;
    }

    public enum Tab {
        main(R.id.layout_tab_main, R.drawable.tab_home, "首页"),

        category(R.id.layout_tab_category, R.drawable.tab_home, "类目"),

        cart(R.id.layout_tab_cart, R.drawable.tab_home, "购物车"),

        profile(R.id.layout_tab_profile, R.drawable.tab_home, "你的"),;

        int containerId;
        int iconRid;
        String labelText;

        Tab(int containerId, int iconRid, String labelText) {
            this.containerId = containerId;
            this.iconRid = iconRid;
            this.labelText = labelText;
        }
    }


}
