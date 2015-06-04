package com.litesuits.socket;

/**
 * Created by taosj on 15/5/22.
 */
public class Message {

    public static final int HEAD_LENGTH = 7;

    public static final byte HEAD = 0x7e;

    public final byte head;
    public final int length;
    public final byte command;
    public final byte[] data;
    public final byte crc;

    public Message(byte head, int length, byte command, byte[] data, byte crc) {
        this.head = head;
        this.length = length;
        this.command = command;
        this.data = data;
        this.crc = crc;
    }
}
