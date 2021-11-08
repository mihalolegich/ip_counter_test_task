package com.shipunovm.ip_counter.smartcounter.filereader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static java.lang.System.out;
import static java.lang.Thread.*;

public class FilePartByPartReaderThread implements Runnable {

    public static final int BYTES_AMNT_TO_READ = 1024 * 1024 * 5;
    private final String fileName;
    private final LinkedBlockingQueue<byte[]> bytesQueue;
    private final ThreadPoolExecutor executor;

    public FilePartByPartReaderThread(String fileName, LinkedBlockingQueue<byte[]> bytesQueue, ThreadPoolExecutor executor) {
        this.fileName = fileName;
        this.bytesQueue = bytesQueue;
        this.executor = executor;
    }

    @Override
    public void run() {
        try (AsynchronousFileChannel fileChannel =
                     AsynchronousFileChannel.open(Paths.get(fileName), new HashSet<>(Collections.singletonList(StandardOpenOption.READ)), executor)) {
            ByteBuffer buffer = ByteBuffer.allocate(BYTES_AMNT_TO_READ);
            AsynchronousFileReader asynchronousFileReader = new AsynchronousFileReader(fileChannel, buffer);
            long startPositionInFile = 0L;
            while (true) {
                final long fileSize = fileChannel.size();
                if (startPositionInFile >= fileSize) break;
                FileReadDataSummary bytesAndLastReadBytePosition =
                        asynchronousFileReader.getBytesAndLastReadBytePosition(startPositionInFile);
                int readBytesAmnt = bytesAndLastReadBytePosition.getLastReadBytePosition();
                startPositionInFile += readBytesAmnt + 1;
                out.printf("%.2f%% of file is read\r\n", startPositionInFile * 1.0 / fileSize * 100);
                bytesQueue.put(Arrays.copyOfRange(bytesAndLastReadBytePosition.getBytes(), 0, readBytesAmnt + 1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            currentThread().interrupt();
        }
    }

}
