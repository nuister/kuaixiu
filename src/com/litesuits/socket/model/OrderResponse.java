package com.litesuits.socket.model;

import java.io.UnsupportedEncodingException;

/**
 * Activity.
 * <p/>
 *
 * @author <a href="shijun.tao@gangsh.com">Shijun Tao</a>
 *         15/6/2
 */
public class OrderResponse extends ModelBase {

    public String sn = "";//16字节
    public String typeCode = "";//8字节
    public int price = 0;//4字节
    public String address;
    public String state = "";
    public String name;
    public String mobile;

    @Override
    protected byte[] getData() {
        StringBuilder sb = new StringBuilder();
        sb.append(sn);
        sb.append(",");
        sb.append(typeCode);
        sb.append(",");
        sb.append(price);
        sb.append(",");
        sb.append(address);
        sb.append(",");
        sb.append(state);
        sb.append(",");
        sb.append(name);
        sb.append(",");
        sb.append(mobile);
        try {
            return sb.toString().getBytes("gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected byte getCommand() {
        return 0x05;
    }

    public static OrderResponse parse(byte[] data) {
        OrderResponse req = new OrderResponse();

        try {
            String order = new String(data, "gb2312");
            String[] orderItems = order.split(",");
            req.sn = orderItems[0];
            req.typeCode = orderItems[1];
            req.price = Integer.parseInt(orderItems[2]);
            req.address = orderItems[3];
            req.state = orderItems[4];
            req.name = orderItems[5];
            req.mobile = orderItems[6];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return req;
    }
}


