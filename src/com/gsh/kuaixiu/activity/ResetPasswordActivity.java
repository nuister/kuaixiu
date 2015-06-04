package com.gsh.kuaixiu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import android.widget.TextView;
import com.gsh.kuaixiu.R;
import com.gsh.base.https.ApiResult;
import com.gsh.base.model.FetchDataListener;
import com.gsh.base.viewmodel.ResetPasswordViewModel;
import com.gsh.base.viewmodel.ResetPasswordViewModel.Entry;
import com.gsh.base.viewmodel.SecurityCodeViewModel;
import com.gsh.kuaixiu.Constant;

/**
 * @author Tan Chunmao
 */
public class ResetPasswordActivity extends KuaixiuActivityBase {
    private EditText mobileInput;
    private EditText passwordInput;
    private EditText codeInput;
    private SecurityCodeViewModel securityCodeModel;
    private TextView captchaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        setTitleBar("找回密码","提交");
        captchaButton=(TextView)findViewById(R.id.click_code);
        mobileInput = (EditText) findViewById(R.id.input_phone);
        passwordInput = (EditText) findViewById(R.id.input_password);
        codeInput = (EditText) findViewById(R.id.input_code);

        captchaButton.setOnClickListener(onClickListener);
        findViewById(R.id.register_delegate).setVisibility(View.INVISIBLE);
        securityCodeModel = new SecurityCodeViewModel(this, Constant.SMS.APPKEY, Constant.SMS.APPSECRET, eventListener);
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (R.id.click_code == v.getId()) {
                requestCode();
            }
        }
    };

    @Override
    protected void onRightActionPressed() {
        super.onRightActionPressed();
        checkCode();
    }

    private Entry entry;
    private void register() {
        entry = new Entry();
        entry.mobile = mobileInput.getText().toString();
        entry.password = passwordInput.getText().toString();
        ResetPasswordViewModel registerViewModel = new ResetPasswordViewModel();
        registerViewModel.regulateInput(entry, fetchDataListener);
    }

    private void checkCode() {
        String phoneInput = mobileInput.getText().toString();
        String codeString = codeInput.getText().toString();
        securityCodeModel.checkCode(phoneInput, codeString);
    }

    private void requestCode() {
        String phoneInput = ((EditText) findViewById(R.id.input_phone)).getText().toString();
        securityCodeModel.requestCode(phoneInput);
    }

    private FetchDataListener fetchDataListener = new FetchDataListener() {
        @Override
        public void onSuccess(ApiResult apiResult) {
            dismissProgressDialog();
            hideKeyboard();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.putExtra(String.class.getName(), entry.mobile);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }, 100);
        }

        @Override
        public void onFailure(String description) {
            dismissProgressDialog();
            toast(description);
        }
    };

    private SecurityCodeViewModel.EventListener eventListener = new SecurityCodeViewModel.EventListener() {
        @Override
        public void onEvent(String s) {
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
}
