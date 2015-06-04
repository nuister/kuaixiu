package com.litesuits.socket.model;

import com.litesuits.socket.utils.ByteConverter;

/**
 * Created by taosj on 15/5/22.
 */
public class LocationRequest extends ModelBase {
    public int lat;
    public int lng;

    @Override
    protected byte[] getData() {
        byte[] data = new byte[8];

        byte[] bLat = ByteConverter.Int2Byte(lat);
        byte[] bLon = ByteConverter.Int2Byte(lng);

        System.arraycopy(bLat, 0, data, 0, 4);
        System.arraycopy(bLon, 0, data, 4, 4);

        return data;
    }

    @Override
    protected byte getCommand() {
        return (byte) 0x84;
    }

    public static LocationRequest parse(byte[] data) {
        LocationRequest location = new LocationRequest();

        location.lat = ByteConverter.bytesToInt(data, 0);
        location.lng = ByteConverter.bytesToInt(data, 4);

        return location;
    }
}
