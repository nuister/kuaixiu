package com.gsh.base.viewmodel;

import android.os.Handler;
import android.os.Message;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.gsh.base.activity.ActivityBase;
import com.litesuits.common.utils.StringUtils;

/**
 * @author Tan Chunmao
 */
public class SecurityCodeViewModel extends ViewModelBase implements Handler.Callback {
    private static final String COUNTRY_CODE_CHINA = "86";
    private ActivityBase activityBase;
    private String appKey;
    private String appSecret;
    private boolean ready;
    private EventListener eventListener;

    public interface EventListener {
        void onEvent(String s);
        void onVerificationThrough();
        void onResponse();
    }

    public SecurityCodeViewModel(ActivityBase activityBase, String appKey, String appSecret, EventListener eventListener) {
        this.activityBase = activityBase;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.eventListener = eventListener;
        initSDK();
    }

    private void initSDK() {
        SMSSDK.initSDK(activityBase, appKey, appSecret);
        final Handler handler = new Handler(this);
        EventHandler eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
        ready = true;
    }

    public void unRegister() {
        if (ready) {
            SMSSDK.unregisterAllEventHandler();
        }

    }

    public void requestCode(String phoneInput) {
        if(!StringUtils.checkPhoneNo(phoneInput)) {
            eventListener.onEvent("手机号码不合法");
        } else {
            SMSSDK.getVerificationCode(COUNTRY_CODE_CHINA, phoneInput);
        }
    }

    public void checkCode(String phoneInput, String codeInput) {
        if(!StringUtils.checkPhoneNo(phoneInput)){
            eventListener.onEvent("手机号码不合法");
        } else if(!codeInput.matches("\\d{4}")) {
            eventListener.onEvent("验证码不合法");
        } else {
            //            方便测试，暂时关闭短信验证
//            SMSSDK.submitVerificationCode(COUNTRY_CODE_CHINA,phoneInput,codeInput);
            eventListener.onVerificationThrough();
        }
    }

    public boolean handleMessage(Message msg) {
        int event = msg.arg1;
        int result = msg.arg2;
        Object data = msg.obj;
        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
            if (result == SMSSDK.RESULT_COMPLETE) {
//                eventListener.onEvent("验证通过");
                eventListener.onVerificationThrough();
            } else {
                eventListener.onEvent("验证失败");
                ((Throwable) data).printStackTrace();
            }
        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
            if (result == SMSSDK.RESULT_COMPLETE) {
                eventListener.onEvent("验证码已发送");
                eventListener.onResponse();
            } else {
                ((Throwable) data).printStackTrace();
                eventListener.onEvent("请求发送验证码失败，请稍候再试");
                eventListener.onResponse();
            }
        }
        return false;
    }
}
