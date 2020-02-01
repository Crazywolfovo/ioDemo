package com.ioDemo.nio.NIOBaseDemo;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


/**
 * @author yzr
 */
public class FileCopyDemo {

    @FunctionalInterface
    interface FileCopyRunner {
        void fileCopy(File source, File target);
    }

    private static final int READ_END = -1;

    private static final int ROUNDS = 5;

    private static void benchmark(FileCopyRunner fileCopyRunner, File source, File target) {
        long elapsed = 0L;
        for (int i = 0; i < ROUNDS; i++) {
            long startTime = System.currentTimeMillis();
            fileCopyRunner.fileCopy(source, target);
            elapsed += System.currentTimeMillis() - startTime;
            target.delete();
        }
        System.out.println("Avg time is :" + elapsed / ROUNDS);
    }

    private static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static FileCopyRunner noBufferStreamCopy = (source, target) -> {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(source);
            outputStream = new FileOutputStream(target);
            int result;
            while (READ_END != (result = inputStream.read())) {
                outputStream.write(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
            close(outputStream);
        }
    };

    private static FileCopyRunner bufferedStreamCopy = (source, target) -> {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(source));
            outputStream = new BufferedOutputStream(new FileOutputStream(target));
            byte[] bytes = new byte[1024];
            int result;
            while (READ_END != (result = inputStream.read(bytes))) {
                outputStream.write(bytes, 0, result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
            close(outputStream);
        }
    };

    private static FileCopyRunner nioBufferCopy = (source, target) -> {
        FileChannel inFileChannel = null;
        FileChannel outFileChannel = null;
        try {
            inFileChannel = new FileInputStream(source).getChannel();
            outFileChannel = new FileOutputStream(target).getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            while (READ_END != (inFileChannel.read(byteBuffer))) {

                byteBuffer.flip();
                while (byteBuffer.hasRemaining()) {
                    outFileChannel.write(byteBuffer);
                }
                byteBuffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(inFileChannel);
            close(outFileChannel);
        }

    };
    /**
     * 两个channel间直接传输数据
     */
    private static FileCopyRunner nioTransferCopy = (source, target) -> {
        FileChannel inFileChannel = null;
        FileChannel outFileChannel = null;
        try {
            inFileChannel = new FileInputStream(source).getChannel();
            outFileChannel = new FileOutputStream(target).getChannel();
            long transferred = 0L;
            long fileSize = inFileChannel.size();
            while (transferred != fileSize) {
                transferred += inFileChannel.transferTo(0, fileSize, outFileChannel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(inFileChannel);
            close(outFileChannel);
        }
    };

    public static void main(String[] args) {
        File source = new File("/Users/yzr/Documents/filecopy/dev.zip");
        File target = new File("/Users/yzr/Documents/filecopy/dev-copy.zip");
//        benchmark(noBufferStreamCopy, source, target);
        benchmark(bufferedStreamCopy, source, target);
        benchmark(nioBufferCopy, source, target);
        benchmark(nioTransferCopy, source, target);
    }
}
