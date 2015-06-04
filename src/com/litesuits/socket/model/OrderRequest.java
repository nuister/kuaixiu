package com.litesuits.socket.model;

import java.io.UnsupportedEncodingException;

/**
 * Activity.
 * <p/>
 *
 * @author <a href="shijun.tao@gangsh.com">Shijun Tao</a>
 *         15/6/2
 */
public class OrderRequest extends ModelBase {

    public String sn;
    public String action;

    @Override
    protected byte[] getData() {
        StringBuilder sb = new StringBuilder();
        sb.append(sn);
        sb.append(",");
        sb.append(action);
        try {
            return sb.toString().getBytes("gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected byte getCommand() {
        return (byte) 0x85;
    }

    public static OrderRequest parse(byte[] data) {
        OrderRequest req = new OrderRequest();

        try {
            String order = new String(data, "gb2312");
            String[] orderItems = order.split(",");
            req.sn = orderItems[0];
            req.action = orderItems[1];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return req;
    }
}
