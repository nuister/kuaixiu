package com.gsh.kuaixiu.activity;

import android.os.Bundle;
import android.view.View;

import com.gsh.kuaixiu.R;
import com.gsh.base.ShareApp;
import com.gsh.kuaixiu.Constant;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;

/**
 * @author Tan Chunmao
 */
public class ShareActivity extends KuaixiuActivityBase {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_share);
        setTitleBar("");
        findViewById(R.id.click_share).setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.click_share == v.getId()) {
                showShareDialog();
            }
        }
    };

    @Override
    protected void share(ShareApp shareApp) {
        shareContent = new ShareContent();
//        shareContent.url = data.shareUrl;
        String url = "http://baidu.com";
        String discoveryId = getIntent().getStringExtra(String.class.getName());
        shareContent.url = String.format(url, discoveryId);
        shareContent.path = Constant.Urls.IMAGE_LOGO;
        Platform.ShareParams sp = new Platform.ShareParams();
        String appName = getString(R.string.app_name);
        sp.setTitle(appName);
        sp.setTitleUrl(shareContent.url);
        String text = getString(R.string.share_test, shareContent.url, "", appName);
        sp.setText(text);
        if (shareApp != ShareApp.wechat)
            sp.setImageUrl(shareContent.path);
        sp.setShareType(Platform.SHARE_IMAGE);
        Platform plat = ShareSDK.getPlatform(shareApp.getPlatformName());
        //plat.setPlatformActionListener(this);
        plat.share(sp);
        super.share(shareApp);
    }
}
