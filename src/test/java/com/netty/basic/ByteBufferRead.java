package com.netty.basic;

import java.nio.ByteBuffer;

import static com.netty.util.ByteBufferUtil.debugAll;

public class ByteBufferRead {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();

        // 从头开始读
        buffer.get(new byte[4]);
        debugAll(buffer);
        buffer.rewind();
        // System.out.println(buffer.get());

        // mark & reset, mark 记录一个 position，reset 重置到该 position
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.mark();
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.reset();
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());

        // get(i)
        System.out.println((char) buffer.get(2));
        debugAll(buffer);
    }
}
