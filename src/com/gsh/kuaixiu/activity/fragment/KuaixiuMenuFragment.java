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

package com.gsh.kuaixiu.activity.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.gsh.kuaixiu.R;
import com.gsh.base.activity.ActivityBase;
import com.gsh.base.activity.fragment.FragmentBase;
import com.gsh.kuaixiu.activity.EmptyActivity;
import com.gsh.kuaixiu.activity.OrderListActivity;
import com.gsh.kuaixiu.activity.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述类的功能
 * <p/>
 *
 * @author Tan Chunmao
 */
public class KuaixiuMenuFragment extends FragmentBase {
    private MyAdapter adapter;

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
        layout = inflater.inflate(R.layout.fragment_kuaixiu_menu, container, false);
        findViewById(R.id.avatar).setOnClickListener(onClickListener);
        initItems();
        return layout;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.avatar == v.getId()) {
                startActivity(new Intent(activity, SettingsActivity.class));
            }
        }
    };

    private void initItems() {
        List<Item> itemList = new ArrayList<Item>();
        itemList.add(new Item(R.drawable.menu_order, "维修订单", OrderListActivity.class));
        itemList.add(new Item(R.drawable.menu_share, "分享获优惠", EmptyActivity.class));
        itemList.add(new Item(R.drawable.menu_coupon, "优惠券", EmptyActivity.class));
        itemList.add(new Item(R.drawable.menu_notification, "通知中心", EmptyActivity.class));
        itemList.add(new Item(R.drawable.menu_about, "关于快修", EmptyActivity.class));
        adapter = new MyAdapter(itemList, activity);
        ListView listView = (ListView) findViewById(R.id.list_menu);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(activity, adapter.getItem(position).aClass);
            intent.putExtra(String.class.getName(), adapter.getItem(position).name);
            startActivity(intent);
        }
    };

    static class Item {
        int iconId;
        String name;
        Class<? extends ActivityBase> aClass;

        public Item(int iconId, String name, Class<? extends ActivityBase> aClass) {
            this.iconId = iconId;
            this.name = name;
            this.aClass = aClass;
        }
    }

    static class MyAdapter extends BaseAdapter {
        private List<Item> menuData;
        private Activity activity;

        public MyAdapter(List<Item> menuData, Activity activity) {
            this.menuData = menuData;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return menuData.size();
        }

        @Override
        public Item getItem(int position) {
            return menuData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = activity.getLayoutInflater().inflate(R.layout.item_menu, null);
                holder = new ViewHolder();
                holder.img = (ImageView) convertView
                        .findViewById(R.id.menuList_img);
                holder.tv = (TextView) convertView.findViewById(R.id.menuList_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            setData(position, holder);
            return convertView;
        }

        private void setData(int position, ViewHolder holder) {
            holder.img.setImageResource(getItem(position).iconId);
            holder.tv.setText(getItem(position).name);
        }

        private class ViewHolder {
            public ImageView img;
            public TextView tv;
        }
    }
}
