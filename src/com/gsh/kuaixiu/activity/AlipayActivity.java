package com.gsh.kuaixiu.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.gsh.kuaixiu.R;
import com.gsh.base.alipay.AlipayReponseHandler;
import com.gsh.base.alipay.AlipayUtils;
import com.gsh.base.alipay.PayItem;
import com.gsh.base.alipay.PayResponseListener;

/**
 * @author Tan Chunmao
 */
public class AlipayActivity extends KuaixiuActivityBase {
    private AlipayReponseHandler alipayResultHandler;

    private PayResponseListener payResponseListener = new PayResponseListener() {
        public void handleMessage(Message msg) {
            String toast = AlipayUtils.handleResponse(msg);
            toast(toast);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_pay);
        setTitleBar("支付测试");
        findViewById(R.id.click_pay).setOnClickListener(onClickListener);
        alipayResultHandler = new AlipayReponseHandler(payResponseListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.click_pay == v.getId()) {
                PayItem payItem = new PayItem("", "", 0.59);
                AlipayUtils.pay(AlipayActivity.this, alipayResultHandler, payItem);
            }
        }
    };
}
