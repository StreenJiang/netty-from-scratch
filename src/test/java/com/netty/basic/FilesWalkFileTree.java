package com.netty.basic;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

public class FilesWalkFileTree {
    public static void main(String[] args) {
        // test1();

        AtomicInteger jarCounter = new AtomicInteger();
        try {
            Files.walkFileTree(Paths.get("./"), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(".jar")) {
                        System.out.println(file);
                        jarCounter.incrementAndGet();
                    }
                    return super.visitFile(file, attrs);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Jar count: = " + jarCounter);
    }

    private static void test1() {
        AtomicInteger dirCounter = new AtomicInteger();
        AtomicInteger fileCounter = new AtomicInteger();
        try {
            Files.walkFileTree(Paths.get("./"), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException {
                    System.out.println(dir.toString());
                    dirCounter.incrementAndGet();
                    return super.preVisitDirectory(dir, attrs);
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    fileCounter.incrementAndGet();
                    return super.visitFile(file, attrs);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Dir count: = " + dirCounter);
        System.out.println("File count: = " + fileCounter);
    }
}
