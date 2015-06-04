package com.litesuits.socket;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.gsh.kuaixiu.Constant;
import com.gsh.kuaixiu.User;
import com.gsh.kuaixiu.activity.OrderDetailNewActivity;
import com.litesuits.android.async.AsyncTask;
import com.litesuits.common.cache.XmlCache;
import com.litesuits.socket.model.LoginRequest;
import com.litesuits.socket.model.LoginResponse;
import com.litesuits.socket.model.LogoutResponse;
import com.litesuits.socket.model.Ping;
import com.litesuits.socket.utils.ByteConverter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by taosj on 15/5/21.
 */
public class TcpClientHandler extends SimpleChannelInboundHandler<Message> {

    private Context context;

    public TcpClientHandler(Context context) {
        this.context = context;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        //messageReceived方法,名称很别扭，像是一个内部方法.
        Log.d("|", "----------------------------下行--------------------------------------");
        Log.d("HEAD", "0x" + ByteConverter.byte2HexString(msg.head));
        Log.d("Length", msg.length + "");
        Log.d("COMMAND", "0x" + ByteConverter.byte2HexString(msg.command));
        Log.d("DATA", "0x" + ByteConverter.bytes2HexString(msg.data));
        Log.d("CRC", "0x" + ByteConverter.byte2HexString(msg.crc));
        Log.d("|", "----------------------------------------------------------------------");

        switch (msg.command) {
            case 0x01: {
                LoginResponse res = LoginResponse.parse(msg.data);
                Log.d("LoginResponse", "status=" + res.status);
            }
            break;
            case 0x02: {
                LogoutResponse res = LogoutResponse.parse(msg.data);
                Log.d("LogoutResponse", "status=" + res.status);
            }
            break;
            case 0x05: {
                String data = new String(msg.data, "gb2312");
                Log.d("data", data);
                Intent intent = new Intent();
                intent.setAction(Constant.Action.ORDER);
                intent.putExtra(String.class.getName(), data);
                context.sendBroadcast(intent);
            }
            break;
            case 0x06: {
                String data = new String(msg.data, "gb2312");
                Intent intent = new Intent();
                intent.setAction(Constant.Action.ORDER);
                intent.putExtra(String.class.getName(), data);
                context.sendBroadcast(intent);
            }
            break;
            case 0x1f: {
                ctx.writeAndFlush(new Ping().build()).sync();
            }
            break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (TcpClient.instance.isAlive()) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TcpClient.instance.sendMsg(new Ping().build());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        LoginRequest request = new LoginRequest();
        request.token = User.load(User.class).getToken();
        request.extra = 1;
        ctx.writeAndFlush(request.build()).sync();
        Log.d("socket", "链接成功");
    }


}