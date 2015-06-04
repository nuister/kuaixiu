package com.litesuits.socket.model;

import java.io.UnsupportedEncodingException;

/**
 * Created by taosj on 15/5/26.
 */
public class LoginResponse extends ModelBase{

    public byte status;//0:成功 1:失败 2:已经注册

    @Override
    protected byte getCommand() {
        return 0x01;
    }

    @Override
    protected byte[] getData() {
        byte[] data = new byte[1];
        data[0] = status;
        return data;
    }

    public static LoginResponse parse(byte[] data) {
        LoginResponse req = new LoginResponse();

        req.status = data[0];

        return req;
    }
}
