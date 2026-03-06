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

                    // 2. 写入数据
                    ByteBuffer buffer = StandardCharsets.UTF_8.encode(sb.toString());
                    int len = client.write(buffer);
                    System.out.println("写入长度：" + len);

                    // 3. 判断是否还有剩余数据未发送完
                    if (buffer.hasRemaining()) {
                        // 4. 注册‘可写事件’
                        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                        // 5. 添加附件
                        key.attach(buffer);
                    }
                } else if (key.isWritable()) {
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel channel = (SocketChannel) key.channel();
                    int len = channel.write(buffer);
                    System.out.println("【可写事件】写入长度：" + len);

                    // 6. 清理操作
                    if (!buffer.hasRemaining()) {
                        key.attach(null);
                        // key.cancel(); // * 为什么不用 cancel？
                        key.interestOps(key.interestOps() ^ SelectionKey.OP_WRITE);
                    }
                }
            }
        }
    }
}
