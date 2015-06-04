package com.litesuits.socket;

import android.content.Context;
import android.util.Log;
import com.gsh.kuaixiu.Constant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;


/**
 * Created by taosj on 15/5/21.
 */
public enum TcpClient {

    instance;

    private boolean isON;

    private String host = Constant.Urls.TCP_HOST;
    private int port= Constant.Urls.TCP_PORT;

    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel channel;

    private Context context;

    private Object lock = new Object();

    public boolean isAlive() {
        synchronized (lock) {
            return isON;
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void connect(String host, int port) {
        synchronized (lock) {
            isON = true;
            this.host = host;
            this.port = port;

            group = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("frameDecoder", new MessageDecoder());
                    pipeline.addLast("frameEncoder", new MessageEncoder());
                    pipeline.addLast("handler", new TcpClientHandler(context));
                }
            });
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        }
    }


    private final Channel getChannel() {
        synchronized (lock) {
            Channel channel = null;
            try {
                channel = bootstrap.connect(host, port).sync().channel();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return channel;
        }
    }

    private void confirm() {
        synchronized (lock) {
            if (channel == null || !channel.isOpen() || !channel.isActive()) {
                if (channel == null)
                    channel = getChannel();
                if (channel != null && !channel.isOpen() && !channel.isActive()) {
                    channel.close();
                    channel.disconnect();
                    channel = getChannel();
                }
            }
        }
    }

    public void sendMsg(Message msg) throws Exception {

        synchronized (lock) {
            {
                confirm();

            }


            if (channel != null) {
                Log.d("channel", channel.toString());
                channel.writeAndFlush(msg).sync();
                Log.d("socket", "发送成功");
            } else {
                Log.d("socket", "消息发送失败,连接尚未建立!");
            }
        }
    }

    public void close() {
        synchronized (lock) {
            isON = false;
            if (channel != null) {
                channel.close();
                channel.disconnect();
            }
            channel = null;
            if (group != null)
                group.shutdownGracefully();
            bootstrap = null;
            group = null;
        }
    }
}