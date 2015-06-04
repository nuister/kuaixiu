package com.gsh.base.alipay;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * @author Tan Chunmao
 */
public class AlipayReponseHandler extends Handler {
    private  WeakReference<PayResponseListener> reference;

    public AlipayReponseHandler(PayResponseListener alipayActivity) {
        reference = new WeakReference<PayResponseListener>(alipayActivity);
    }

    public void handleMessage(Message msg) {
        PayResponseListener service = reference.get();
        if (service != null) {
            service.handleMessage(msg);
        }
    }
}
