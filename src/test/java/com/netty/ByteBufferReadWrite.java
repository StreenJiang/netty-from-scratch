package com.netty;

import java.nio.ByteBuffer;

import static com.netty.util.ByteBufferUtil.debugAll;

public class ByteBufferReadWrite {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61); // 'a'
        debugAll(buffer);

        buffer.put(new byte[] {0x62, 0x63, 0x64});
        debugAll(buffer);

        buffer.flip();
        System.out.println(buffer.get());
        debugAll(buffer);

        buffer.compact();
        debugAll(buffer);
    }
}
