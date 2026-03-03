package com.netty.basic;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.netty.util.ByteBufferUtil.debugAll;

public class ByteBufferString {
    public static void main(String[] args) {
        // 1. 字符串转为 ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put("hello".getBytes());
        debugAll(buffer);

        // 2. Charset
        ByteBuffer hello2 = StandardCharsets.UTF_8.encode("hello2");
        debugAll(hello2);

        // 3. wrap
        ByteBuffer hello3 = ByteBuffer.wrap("hello3".getBytes());
        debugAll(hello3);

        String str2 = StandardCharsets.UTF_8.decode(hello2).toString();
        System.out.println(str2);

        buffer.flip(); // 直接读 ByteBuffer 需要切换到读模式
        String str1 = StandardCharsets.UTF_8.decode(buffer).toString();
        System.out.println(str1);
    }
}
