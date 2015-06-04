package com.litesuits.socket.model;

import java.io.UnsupportedEncodingException;

/**
 * Created by taosj on 15/5/26.
 */
public class LoginRequest extends ModelBase {

//    public String imei;//固定32字节,不足32位，在左边加零
    public String token;//固定32字节
    public byte extra;

    @Override
    protected byte[] getData() {
        byte[] data = new byte[33];
        try {
            byte[] tokenBytes = token.getBytes("ASCII");

            System.arraycopy(tokenBytes, 0, data, 0, 32);
            data[32] = extra;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected byte getCommand() {
        return (byte) 0x81;
    }

    public static LoginRequest parse(byte[] data) {
        LoginRequest req = new LoginRequest();

        try {
//            req.imei = new String(data, 0, 32, "ASCII");
            req.token = new String(data, 0, 32, "ASCII");
            req.extra = data[32];
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return req;
    }
}
