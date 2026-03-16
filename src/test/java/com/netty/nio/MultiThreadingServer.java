package com.netty.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.netty.util.ByteBufferUtil.debugAll;

@Slf4j
public class MultiThreadingServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("Boss");

        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);

        Selector boss = Selector.open();
        server.register(boss, SelectionKey.OP_ACCEPT, null);
        // bossKey.interestOps(SelectionKey.OP_ACCEPT); // Same as the second parameter in register method

        server.bind(new InetSocketAddress(8080));

        Worker worker = new Worker("Work-0");
        while (true) {
            boss.select();

            Iterator<SelectionKey> it = boss.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                if (key.isAcceptable()) {
                    SocketChannel client = server.accept();
                    client.configureBlocking(false);
                    log.debug("Connected to {}...", client.getRemoteAddress());

                    // 关联 work
                    log.debug("Before registering to {}...", client.getRemoteAddress());
                    worker.register(client);
                    log.debug("After registering to {}...", client.getRemoteAddress());
                }
            }
        }
    }

    static class Worker implements Runnable {
        private Thread thread;
        private Selector selector;
        private String name;
        private boolean started = false;
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.name = name;
        }

        // 初始化线程和selector
        public void register(SocketChannel client) throws IOException {
            if (!started) {
                selector = Selector.open();

                thread = new Thread(this, name);
                thread.start();

                started = true;
            }

            // 向队列中添加任务
            queue.add(() -> {
                try {
                    client.register(selector, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            // 任务添加之后，唤醒 selector
            selector.wakeup();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();

                    Runnable task = queue.poll();
                    if (task != null) {
                        task.run(); // 执行任务
                    }

                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();

                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();

                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel client = (SocketChannel) key.channel();
                            log.debug("Data read from {}...", client.getRemoteAddress());

                            client.read(buffer);
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
