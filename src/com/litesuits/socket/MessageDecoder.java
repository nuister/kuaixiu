package com.litesuits.socket;

import com.litesuits.android.log.Log;
import com.litesuits.socket.model.Ping;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by taosj on 15/5/22.
 */
public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in instanceof EmptyByteBuf) {
            Log.d("EMPTYBYTEBUF","is EmptyByteBuf");
//            TcpClient.instance.sendMsg(new Ping().build());
        }
        if (in.readableBytes() < Message.HEAD_LENGTH)
            return;
        in.markReaderIndex();
        byte head = in.readByte();
        if (head != Message.HEAD)
            return;
        int length = in.readInt();

        byte command = in.readByte();

        byte[] data = new byte[length - 2];
        in.readBytes(data);

        byte crc = in.readByte();

        Message message = new Message(head, length, command, data, crc);

        if (message.head == (byte) 0x7e && check(message.command, message.data, message.crc))
            out.add(message);
    }


    private boolean check(byte command, byte[] data, byte crc) {
        int res = 0;
        for (int i = 0; i < data.length; i++) {
            res = (i == 0) ? (command ^ data[i])
                    : (res ^ data[i]);
        }
        return (byte) res == (byte) crc;
    }
}
