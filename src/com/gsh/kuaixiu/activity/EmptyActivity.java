package com.gsh.kuaixiu.activity;

import android.os.Bundle;
import com.gsh.kuaixiu.R;

/**
 * Created by Administrator on 2015/5/21.
 */
public class EmptyActivity extends KuaixiuActivityBase {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        setTitleBar(getIntent().getStringExtra(String.class.getName()));
    }
}
