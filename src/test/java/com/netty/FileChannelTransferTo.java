package com.netty;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileChannelTransferTo {
    public static void main(String[] args) {
        try (
                FileChannel from = new FileInputStream("src/test/resources/data1.txt").getChannel();
                FileChannel to = new FileOutputStream("src/test/resources/to.txt").getChannel();
        ) {
            // 效率高，底层会使用操作系统的零拷贝进行优化
            // 传输上限 2GB 数据
            long size = from.size();
            for (long left = size; left > 0; ) {
                left -= from.transferTo((size - left), left, to);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
