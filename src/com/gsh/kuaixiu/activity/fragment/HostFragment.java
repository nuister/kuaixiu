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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.gsh.kuaixiu.R;
import com.gsh.base.activity.MainActivity;
import com.gsh.base.activity.fragment.FragmentBase;

public class HostFragment extends FragmentBase {
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
        layout = inflater.inflate(R.layout.kuaixiu_fragment_host, container, false);
        findViewById(R.id.menu).setOnClickListener(onClickListener);
        for (Type type : Type.values()) {
            View action = findViewById(type.actionId);
            action.setTag(type);
            action.setOnClickListener(onClickListener);
            ((TextView) findViewById(type.labelId)).setText(type.label);
        }
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (R.id.menu == v.getId()) {
                ((MainActivity) activity).getSlidingMenu().showMenu();
            } else if (R.id.click_post == v.getId()) {

            } else {
                chooseType = (Type) v.getTag();
                for (Type type : Type.values()) {
                    findViewById(type.imageId).setSelected(type == chooseType);
                    findViewById(type.labelId).setSelected(type == chooseType);
                }
            }
        }
    };

    private Type chooseType;

    enum Type {
        lock(R.id.type_a, R.id.type_a_image, R.id.type_a_label, "开锁换锁", 0),
        appliance(R.id.type_a, R.id.type_a_image, R.id.type_a_label, "家电维修", 0),
        family(R.id.type_a, R.id.type_a_image, R.id.type_a_label, "家庭小修", 0),;

        int actionId;
        int imageId;
        int labelId;
        String label;
        int param;

        Type(int actionId, int imageId, int labelId, String label, int param) {
            this.actionId = actionId;
            this.imageId = imageId;
            this.labelId = labelId;
            this.label = label;
            this.param = param;
        }
    }
}
