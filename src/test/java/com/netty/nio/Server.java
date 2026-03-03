package com.netty.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static com.netty.util.ByteBufferUtil.debugRead;

@Slf4j
public class Server {
    public static void main(String[] args) {
        // 使用 NIO 来理解阻塞模式，单线程

        // 0. ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 1. 创建服务器
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            // 2. 绑定监听端口
            ssc.bind(new InetSocketAddress(8080));

            // 3. 连接集合
            List<SocketChannel> channels = new ArrayList<>();

            while (true) {
                // 4. SocketChannel 建立与客户端的连接，SocketChannel 用来与客户端之间通信
                log.debug("Connecting...");
                SocketChannel sc = ssc.accept();
                channels.add(sc);
                log.debug("Connected: {}", sc);

                for (SocketChannel channel : channels) {
                    // 5. 接收客户端发送的数据
                    log.debug("Before reading message from client {}...", channel);
                    channel.read(buffer);
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    log.debug("After reading message from client {}...", channel);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
