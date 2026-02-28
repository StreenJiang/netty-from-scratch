package com.netty;


import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class ByteBufferTest {
    public static void main(String[] args) {
        // FileChannel
        // 1. 输入输出流
        // 2. RandomAccessFile
        try (FileChannel fileChannel = new FileInputStream("src/test/resources/data1.txt").getChannel()) {
            // 准备缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(10);

            while (true) {
                // 从 Channel 中读取数据
                int len = fileChannel.read(byteBuffer);
                log.info("读取到的字节数：{}", len);
                if (len == -1) {
                    break;
                }

                // 打印 buffer 的数据
                byteBuffer.flip(); // 切换到读模式
                while (byteBuffer.hasRemaining()) {
                    byte b = byteBuffer.get();
                    log.info("实际字节：{}", (char) b);
                }

                byteBuffer.clear(); // 切换到写模式
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
