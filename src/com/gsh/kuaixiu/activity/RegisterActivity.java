package com.gsh.kuaixiu.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;

import android.widget.TextView;
import com.gsh.kuaixiu.R;
import com.gsh.base.https.ApiResult;
import com.gsh.base.model.FetchDataListener;
import com.gsh.base.viewmodel.RegisterViewModel;
import com.gsh.base.viewmodel.SecurityCodeViewModel;
import com.gsh.kuaixiu.Constant;
import com.litesuits.common.utils.TelephoneUtil;

import java.util.UUID;

/**
 * @author Tan Chunmao
 */
public class RegisterActivity extends KuaixiuActivityBase {
    private EditText mobileInput;
    private EditText passwordInput;
    private EditText codeInput;
    private SecurityCodeViewModel securityCodeModel;
    private TextView captchaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitleBar("注册");
        captchaButton = (TextView) findViewById(R.id.click_code);
        mobileInput = (EditText) findViewById(R.id.input_phone);
        passwordInput = (EditText) findViewById(R.id.input_password);
        codeInput = (EditText) findViewById(R.id.input_code);
        findViewById(R.id.click_register).setOnClickListener(onClickListener);
        findViewById(R.id.click_code).setOnClickListener(onClickListener);
        findViewById(R.id.click_agreement).setOnClickListener(onClickListener);
        securityCodeModel = new SecurityCodeViewModel(this, Constant.SMS.APPKEY, Constant.SMS.APPSECRET, eventListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (R.id.click_register == v.getId()) {
                checkCode();
            } else if (R.id.click_code == v.getId()) {
                requestCode();
            } else if (R.id.click_agreement == v.getId()) {
                go.name(WebActivity.class).param("title", "陌邻快修服务协议").param("html", "protocol.html").go();
            }
        }
    };

    private void register() {
        String mobile = mobileInput.getText().toString();
        String password = passwordInput.getText().toString();
        RegisterViewModel registerViewModel = new RegisterViewModel();
        registerViewModel.mobile = mobile;
        registerViewModel.password = password;
        registerViewModel.deviceId = TelephoneUtil.getIMEI(context);
        registerViewModel.regulateInput(fetchDataListener);
    }

    private void checkCode() {
        String phoneInput = mobileInput.getText().toString();
        String codeString = codeInput.getText().toString();
        showProgressDialog();
        securityCodeModel.checkCode(phoneInput, codeString);
    }

    private void requestCode() {
        String phoneInput = ((EditText) findViewById(R.id.input_phone)).getText().toString();
        showProgressDialog();
        securityCodeModel.requestCode(phoneInput);
    }


    private FetchDataListener fetchDataListener = new FetchDataListener() {
        @Override
        public void onSuccess(ApiResult apiResult) {
            toast("注册成功");
            dismissProgressDialog();
            hideKeyboard();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(context, KuaixiuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }, 100);
        }

        @Override
        public void onFailure(String description) {
            dismissProgressDialog();
            toast(description);
        }
    };

    private void timer() {
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                captchaButton.setClickable(Boolean.FALSE);
                captchaButton.setText("(" + l / 1000 + ")秒后获取");
            }

            @Override
            public void onFinish() {
                captchaButton.setClickable(Boolean.TRUE);
                captchaButton.setText("获取验证码");
            }
        }.start();
    }


    private SecurityCodeViewModel.EventListener eventListener = new SecurityCodeViewModel.EventListener() {
        @Override
        public void onEvent(String s) {
            dismissProgressDialog();
            toast(s);
        }

        @Override
        public void onVerificationThrough() {
            register();
        }

        @Override
        public void onResponse() {
            timer();
        }
    };
}
