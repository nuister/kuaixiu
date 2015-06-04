package com.litesuits.socket.model;

import java.io.UnsupportedEncodingException;

/**
 * Created by taosj on 15/5/26.
 */
public class LogoutRequest extends ModelBase {

    //    public String imei;//固定32字节,不足32位，在左边加零
    public String token;//固定32字节

    @Override
    protected byte[] getData() {
        byte[] data = new byte[32];
        try {
//            byte[] imeiBytes = imei.getBytes("ASCII");
            byte[] tokenBytes = token.getBytes("ASCII");

//            System.arraycopy(imeiBytes, 0, data, 0, 32);
            System.arraycopy(tokenBytes, 0, data, 0, 32);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected byte getCommand() {
        return (byte) 0x82;
    }

    public static LogoutRequest parse(byte[] data) {
        LogoutRequest req = new LogoutRequest();

        try {
//            req.imei = new String(data, 0, 32, "ASCII");
            req.token = new String(data, 0, 32, "ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return req;
    }
}
