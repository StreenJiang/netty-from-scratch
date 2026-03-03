package com.netty.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.netty.util.ByteBufferUtil.debugAll;
import static com.netty.util.ByteBufferUtil.debugRead;

@Slf4j
public class ServerWithSelector {
    public static void main(String[] args) throws IOException {
        // 1. 创建 Selector，管理多个 Channel
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 2. 建立 Selector 和 Channel 的联系（注册）
        // SelectionKey 通过 key 可以知道事件类型和对应的 Channel
        SelectionKey key = ssc.register(selector, 0, null);
        // 只关注 accept 事件
        key.interestOps(SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8080));

        while (true) {
            // 3. select 方法，没有事件时是阻塞的，有事件才会继续执行
            // 存在未处理事件时不会阻塞线程
            // 要么处理事件，要么取消事件，否则会一直不阻塞
            selector.select();

            // 4. 处理事件，包含了所有发生的事件
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                // 必须手动删除，否则事件将一直存在于列表中，即使已经处理过
                // 不删除会导致错误的判断，比如 acceptable 会一直触发
                // * 思考：我在想这个删除是不是应该确定事件正确处理完毕再调用，而不是在这？
                iterator.remove();

                if (next.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) next.channel();
                    SocketChannel client = server.accept();
                    client.configureBlocking(false);
                    SelectionKey clientKey = client.register(selector, 0, ByteBuffer.allocate(16));
                    clientKey.interestOps(SelectionKey.OP_READ);
                } else if (next.isReadable()) {
                    try {
                        SocketChannel client = (SocketChannel) next.channel();
                        ByteBuffer buffer = (ByteBuffer) next.attachment();
                        int len = client.read(buffer);
                        if (len == -1) {
                            next.cancel(); // 正常断开
                        } else {
                            split(buffer);
                            // 判断是否需要扩容
                            // （即数据超过 buffer 的长度，无法一次性读完，切没有消耗掉先读到的部分导致无法继续读取后续部分）
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                next.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        next.cancel(); // 异常断开
                    }
                }
            }
        }
    }

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if (source.get(i) == '\n') {
                int len = i + 1 - source.position();
                // 存入一个新的 ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(len);
                // 读取
                for (int j = 0; j < len; j++) {
                    target.put(source.get());
                }

                debugAll(target);
            }
        }

        // 不清除，保留之前没处理完的数据
        source.compact();
    }
}
