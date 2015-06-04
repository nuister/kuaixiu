package com.gsh.base.alipay;

import android.app.Activity;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.gsh.kuaixiu.Constant;
import com.gsh.kuaixiu.KuaixiuApplication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * @author Tan Chunmao
 */
public final class AlipayUtils {
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;

    private static final String RESPONSE_SUCCESS = "9000";

    private static final String RESPONSE_WAIT = "8000";

    private AlipayUtils() {
    }

    public static void pay(final Activity activity, final AlipayReponseHandler handler, PayItem payItem) {

        String orderInfo = getOrderInfo(payItem);

        String sign = sign(orderInfo);
        try {

            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                String result = alipay.pay(payInfo);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        };


        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    private static String getSignType() {
        return "sign_type=\"RSA\"";
    }

    private static String getOrderInfo(PayItem payItem) {

        String orderInfo = "partner=" + "\"" + Constant.Alipay.PARTNER + "\"";

        orderInfo += "&seller_id=" + "\"" + Constant.Alipay.SELLER + "\"";

        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        orderInfo += "&subject=" + "\"" + payItem.getTitle() + "\"";

        orderInfo += "&body=" + "\"" + payItem.getSummary() + "\"";

        orderInfo += String.format("&total_fee=\"%.2f\"", payItem.getPrice());

        orderInfo += String.format("&notify_url=\"%s\"", Constant.Alipay.NOTIFY_URL);

        orderInfo += "&service=\"mobile.securitypay.pay\"";

        orderInfo += "&payment_type=\"1\"";

        orderInfo += "&_input_charset=\"utf-8\"";

        orderInfo += "&it_b_pay=\"30m\"";

        orderInfo += String.format("&return_url=\"%s\"", Constant.Alipay.REDIRECT_URL);

        return orderInfo;
    }

    private static String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
                Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    public static void payByAlipay(String key, String value, final Activity activity,final AlipayReponseHandler handler) {
        final String payInfo = key + "&sign=\"" + value + "\"&"
                + getSignType();
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(activity);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    private static String sign(String content) {
        return SignUtils.sign(content, Constant.Alipay.RSA_PRIVATE);
    }

    public static String handleResponse(Message msg) {
        String toast = "";
        switch (msg.what) {
            case AlipayUtils.SDK_PAY_FLAG: {
                PayResult payResult = new PayResult((String) msg.obj);
                String resultStatus = payResult.getResultStatus();
                if (TextUtils.equals(resultStatus, AlipayUtils.RESPONSE_SUCCESS)) {
                    toast = "支付成功";
                } else {
                    if (TextUtils.equals(resultStatus, AlipayUtils.RESPONSE_WAIT)) {
                        toast = "支付结果确认中";
                    } else {
                        toast = "支付失败";
                    }
                }
                break;
            }
            case AlipayUtils.SDK_CHECK_FLAG: {
                toast = "检查结果为：" + msg.obj;
                break;
            }
            default:
                break;
        }
        return toast;
    }
}
