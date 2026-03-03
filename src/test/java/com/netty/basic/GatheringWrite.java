package com.netty.basic;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import static com.netty.util.ByteBufferUtil.debugAll;

public class GatheringWrite {
    public static void main(String[] args) {
        ByteBuffer b1 = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer b2 = StandardCharsets.UTF_8.encode("world");
        ByteBuffer b3 = StandardCharsets.UTF_8.encode("你好");

        try (FileChannel channel = new RandomAccessFile("src/test/resources/word2.txt", "rw").getChannel()) {
            channel.write(new ByteBuffer[]{b1, b2, b3});

            debugAll(b1);
            debugAll(b2);
            debugAll(b3);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
