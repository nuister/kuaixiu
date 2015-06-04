package com.litesuits.socket.model;

/**
 * Activity.
 * <p/>
 *
 * @author <a href="shijun.tao@gangsh.com">Shijun Tao</a>
 *         15/5/27
 */
public class Ping extends ModelBase{

    @Override
    protected byte[] getData() {
        byte[] data = new byte[1];
        data[0] = 0x00;
        return data;
    }

    @Override
    protected byte getCommand() {
        return (byte)0x1f;
    }
}
