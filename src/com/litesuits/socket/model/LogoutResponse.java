package com.litesuits.socket.model;

/**
 * Created by taosj on 15/5/26.
 */
public class LogoutResponse extends ModelBase{

    public byte status;//0:成功 1:失败 2:已经注销

    @Override
    protected byte getCommand() {
        return 0x02;
    }

    @Override
    protected byte[] getData() {
        byte[] data = new byte[1];
        data[0] = status;
        return data;
    }

    public static LogoutResponse parse(byte[] data) {
        LogoutResponse req = new LogoutResponse();

        req.status = data[0];

        return req;
    }
}
