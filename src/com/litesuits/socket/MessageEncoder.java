package com.litesuits.socket;

import com.litesuits.socket.utils.ByteConverter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by taosj on 15/5/22.
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {


        out.writeByte(msg.head);

        out.writeInt(msg.length);

        out.writeByte(msg.command);


        out.writeBytes(msg.data);

        out.writeByte(msg.crc);

        ctx.flush();

        android.util.Log.d("|", "----------------------------上行--------------------------------------");
        android.util.Log.d("HEAD", "0x" + ByteConverter.byte2HexString(msg.head));
        android.util.Log.d("Length", msg.length + "");
        android.util.Log.d("COMMAND", "0x" + ByteConverter.byte2HexString(msg.command));
        android.util.Log.d("DATA", "0x" + ByteConverter.bytes2HexString(msg.data));
        android.util.Log.d("CRC", "0x" + ByteConverter.byte2HexString(msg.crc));
        android.util.Log.d("|", "----------------------------------------------------------------------");
    }


}
