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
public class ServerNIO {
    public static void main(String[] args) {
        // 非阻塞模式，单线程

        // 0. ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 1. 创建服务器
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            // 设置为非阻塞（即 NIO - NonBlocking IO）
            ssc.configureBlocking(false);

            // 2. 绑定监听端口
            ssc.bind(new InetSocketAddress(8080));

            // 3. 连接集合
            List<SocketChannel> channels = new ArrayList<>();

            while (true) {
                // 4. SocketChannel 建立与客户端的连接，SocketChannel 用来与客户端之间通信
                SocketChannel sc = ssc.accept(); // 非阻塞情况下，没有连接则返回 null
                if (sc != null) {
                    channels.add(sc);
                    // 设置非阻塞，则 Read 就不阻塞了
                    sc.configureBlocking(false);
                    log.debug("Connected: {}", sc);
                }

                for (SocketChannel channel : channels) {
                    // 5. 接收客户端发送的数据
                    int len = channel.read(buffer);// 非阻塞模式下没读到数据会返回0
                    if (len > 0) {
                        buffer.flip();
                        debugRead(buffer);
                        buffer.clear();
                        log.debug("After reading message from client {}...", channel);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
