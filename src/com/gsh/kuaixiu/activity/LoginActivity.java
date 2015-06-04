package com.gsh.kuaixiu.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import com.gsh.base.https.ApiResult;
import com.gsh.base.model.FetchDataListener;
import com.gsh.base.viewmodel.LoginViewModel;
import com.gsh.base.viewmodel.LoginViewModel.Entry;
import com.gsh.kuaixiu.Constant;
import com.gsh.kuaixiu.R;
import com.litesuits.common.utils.TelephoneUtil;

import java.util.UUID;

/**
 * @author Tan Chunmao
 */
public class LoginActivity extends KuaixiuActivityBase {
    private EditText mobileInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitleBar("登录", "注册账号");
        mobileInput = (EditText) findViewById(R.id.input_phone);
        passwordInput = (EditText) findViewById(R.id.input_password);
        findViewById(R.id.click_login).setOnClickListener(onClickListener);
        findViewById(R.id.click_reset).setOnClickListener(onClickListener);
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (R.id.click_login == v.getId()) {
                Entry entry = new Entry();
                entry.mobile = mobileInput.getText().toString();
                entry.password = passwordInput.getText().toString();
                entry.deviceId = TelephoneUtil.getIMEI(context);

                LoginViewModel loginModel = new LoginViewModel();
                showProgressDialog();
                loginModel.regulateInput(entry, fetchDataListener);

            /*    TcpClient.instance.connect("192.168.0.103", 7777);
                LoginRequest req = new LoginRequest();
                req.imei = "08FA3607196AF0778BEB5C5E103F9B6D";
                req.token = "08FA3607196AF0778BEB5C5E103F9B6D";
                try {
                    TcpClient.instance.sendMsg(req.build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                TcpClient.instance.close();*/

            } else if (R.id.click_reset == v.getId()) {
                go.name(ResetPasswordActivity.class).goForResult(Constant.Request.RESET_PASSWORD);
            }
        }
    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (Constant.Request.RESET_PASSWORD == requestCode) {
                mobileInput.setText(data.getStringExtra(String.class.getName()));
            }
        }
    }
    @Override
    protected void onRightActionPressed() {
        super.onRightActionPressed();
        go.name(RegisterActivity.class).go();
    }

    private FetchDataListener fetchDataListener = new FetchDataListener() {
        @Override
        public void onSuccess(ApiResult apiResult) {
            dismissProgressDialog();
            toast("登录成功");
            hideKeyboard();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    go.name(KuaixiuActivity.class).goAndFinishCurrent();
                }
            }, 100);
        }

        @Override
        public void onFailure(String description) {
            dismissProgressDialog();
            toast(description);
        }
    };

    private String getDeviceId() {
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }
}
