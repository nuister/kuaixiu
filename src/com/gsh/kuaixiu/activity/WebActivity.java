package com.gsh.kuaixiu.activity;

import android.os.Bundle;
import android.webkit.WebView;

import com.gsh.kuaixiu.R;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.common.io.FileUtils;

/**
 * Created by taosj on 15/3/26.
 */
public class WebActivity extends KuaixiuActivityBase {

    @InjectView
    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        Injector.self.inject(this);
        String title = getIntent().getStringExtra("title");
        setTitleBar(title);
        String htmlFile = getIntent().getStringExtra("html");

        setTitleBar(false, title, KuaixiuActivityBase.RightAction.NONE, "");
        String htmlString = FileUtils.getAssetText(this, htmlFile);
        web.loadDataWithBaseURL(null, htmlString, "text/html", "utf-8", null);
    }
}
