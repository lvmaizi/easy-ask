package com.lvmaizi.easy.ask.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BytesUtil {
    public static byte[] floatArrayToByteArray(float[] input) {
        ByteBuffer buffer = ByteBuffer.allocate(input.length * Float.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (float f : input) {
            buffer.putFloat(f);
        }
        return buffer.array();
    }
}
