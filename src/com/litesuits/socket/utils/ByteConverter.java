package com.litesuits.socket.utils;

/**
 * Created by taosj on 15/5/21.
 */
public final class ByteConverter {

    public static byte[] Short2Byte(short send) {
        byte[] bTemp = new byte[2];
        for (int i = 0; i < 2; i++) {
            bTemp[i] = (byte) ((send >> (i * 8)) & 0xff);
        }
        return bTemp;
    }

    public static int twoBitToInt(int low, int high) {
        if (low == 0 && high == 0)
            return 0;
        if (low > 0 && high == 0)
            return 1;
        if (low == 0 && high > 0)
            return 2;
        return 3;
    }

    public static byte[] Int2Byte(int res) {
        byte[] targets = new byte[4];
        targets[0] = (byte) (res >>> 24);
        targets[1] = (byte) ((res >> 16) & 0xff);
        targets[2] = (byte) ((res >> 8) & 0xff);
        targets[3] = (byte) (res & 0xff);
        return targets;
    }

    public static int bytesToInt(byte[] data, int offset) {
        int num = 0;
        for (int i = offset; i < offset + 4; i++) {
            num <<= 8;
            num |= (data[i] & 0xff);
        }
        return num;
    }

    public static byte[] long2Byte(long x) {
        byte[] bb = new byte[8];
        bb[0] = (byte) (x >> 56);
        bb[1] = (byte) (x >> 48);
        bb[2] = (byte) (x >> 40);
        bb[3] = (byte) (x >> 32);
        bb[4] = (byte) (x >> 24);
        bb[5] = (byte) (x >> 16);
        bb[6] = (byte) (x >> 8);
        bb[7] = (byte) (x >> 0);
        return bb;
    }

    public static String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public static String byte2HexString(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex.toUpperCase();
    }
}
