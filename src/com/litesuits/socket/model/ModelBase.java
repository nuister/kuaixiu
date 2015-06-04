package com.litesuits.socket.model;

import com.litesuits.socket.Message;

/**
 * Created by taosj on 15/5/26.
 */
public abstract class ModelBase {

    protected abstract byte[] getData();

    protected abstract byte getCommand();

    public final Message build() {

        //长度从命令标识符到结尾
        int length = 1 + getData().length + 1;

        Message message = new Message((byte) 0x7e, length, getCommand(), getData(), getCheckCode(getCommand(), getData()));

        return message;
    }

    private byte getCheckCode(byte command, byte[] data) {
        int res = 0;
        for (int i = 0; i < data.length; i++) {
            res = (i == 0) ? (command ^ data[i])
                    : (res ^ data[i]);
        }
        return (byte) res;
    }


}
