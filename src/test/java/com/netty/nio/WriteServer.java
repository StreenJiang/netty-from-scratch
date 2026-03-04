package com.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class WriteServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);

        Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT, null);
        server.bind(new InetSocketAddress(8080));

        while (true) {
            selector.select();
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();

                if (key.isAcceptable()) {
                    // Same as: SocketChannel client = ((ServerSocketChannel) key.channel()).accept();
                    SocketChannel client = server.accept();
                    client.configureBlocking(false);

                    // 1. 向客户端发送大量数据
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < 30000000; i++) {
                        sb.append("a");
                    }

                    ByteBuffer buffer = StandardCharsets.UTF_8.encode(sb.toString());
                    while (buffer.hasRemaining()) {
                        int len = client.write(buffer);
                        // 2
                        System.out.println("写入长度：" + len);
                    }
                }
            }
        }
    }
}
