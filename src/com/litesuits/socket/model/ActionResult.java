package com.litesuits.socket.model;

import java.io.UnsupportedEncodingException;


/**
 * Activity.
 * <p/>
 *
 * @author <a href="shijun.tao@gangsh.com">Shijun Tao</a>
 *         15/6/4
 */
public class ActionResult extends ModelBase {

    public String sn;
    public String action;
    public String status;

    @Override
    protected byte[] getData() {
        StringBuilder sb = new StringBuilder();
        sb.append(sn);
        sb.append(",");
        sb.append(action);
        sb.append(",");
        sb.append(status);

        try {
            return sb.toString().getBytes("gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected byte getCommand() {
        return 0x06;
    }

    public static ActionResult parse(byte[] data) {
        ActionResult req = new ActionResult();

        try {
            String content = new String(data, "gb2312");
            String[] items = content.split(",");
            req.sn = items[0];
            req.action = items[1];
            req.status = items[2];
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return req;
    }
}
