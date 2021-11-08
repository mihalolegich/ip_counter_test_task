package com.shipunovm.ip_counter.smartcounter;

import com.shipunovm.ip_counter.UniqueIPsCounterInFile;
import com.shipunovm.ip_counter.smartcounter.filereader.FilePartByPartReaderThread;
import com.shipunovm.ip_counter.smartcounter.parser.FilePartParserCallable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static java.lang.Thread.*;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.ForkJoinPool.*;
import static java.util.concurrent.TimeUnit.SECONDS;

public class SmartUniqueIPsCounterInFile extends UniqueIPsCounterInFile {

    private static final int MAX_OCTET_VAL = 256;
    private final String fileName;

    public SmartUniqueIPsCounterInFile(String fileName) {
        this.fileName = fileName;
    }

    public long countUniqueIPsInFile() {
        boolean[][][][] finalArray = new boolean[MAX_OCTET_VAL][MAX_OCTET_VAL][MAX_OCTET_VAL][MAX_OCTET_VAL];
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        LinkedBlockingQueue<byte[]> readBytesQueue = new LinkedBlockingQueue<>(availableProcessors);
        LinkedBlockingQueue<Future<Boolean>> filePartsProcessingResults = new LinkedBlockingQueue<>(availableProcessors);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(availableProcessors, availableProcessors,
                60L, SECONDS, new LinkedBlockingQueue<>(availableProcessors));

        Thread filePartsReaderThread = new Thread(new FilePartByPartReaderThread(fileName, readBytesQueue, executor));
        filePartsReaderThread.start();

        Thread filePartsProcessorThread =
                new Thread(() -> processFileParts(readBytesQueue, finalArray, filePartsProcessingResults, filePartsReaderThread));
        filePartsProcessorThread.start();

        Thread processedTasksRemover = new Thread(() -> {
            while (filePartsProcessorThread.isAlive()) {
                filePartsProcessingResults.removeIf(Future::isDone);
            }
        });
        processedTasksRemover.start();

        waitForThreadEnd(filePartsReaderThread);
        waitForAllWhoProcessingFileParts(filePartsProcessingResults);
        executor.shutdown();
        return getUniqueIPsCount(finalArray);
    }

    public void waitForThreadEnd(Thread waitedThread) {
        try {
            waitedThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            currentThread().interrupt();
        }
    }

    private void processFileParts(Queue<byte[]> readBytesQueue,
                                  boolean[][][][] finalArray,
                                  LinkedBlockingQueue<Future<Boolean>> futures,
                                  Thread fileReaderThread) {
        while (!readBytesQueue.isEmpty() || fileReaderThread.isAlive()) {
            for (byte[] bytes : readBytesQueue) {
                try {
                    futures.put(commonPool().submit(new FilePartParserCallable(finalArray, bytes)));
                    readBytesQueue.remove(bytes);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    currentThread().interrupt();
                }
            }
        }
    }

    private void waitForAllWhoProcessingFileParts(Queue<Future<Boolean>> futures) {
        try {
            for (Future<Boolean> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            currentThread().interrupt();
        }
    }

    public long getUniqueIPsCount(boolean[][][][] finalArray) {
        final List<boolean[][][]> booleans = Arrays.asList(finalArray);
        Optional<Long> result = booleans.parallelStream().map(e -> {
            long uniqueIPsCount = 0L;
            final int i = booleans.indexOf(e);
            for (int j = 0; j < MAX_OCTET_VAL; j++) {
                for (int k = 0; k < MAX_OCTET_VAL; k++) {
                    for (int l = 0; l < MAX_OCTET_VAL; l++) {
                        uniqueIPsCount = finalArray[i][j][k][l] ? uniqueIPsCount + 1L : uniqueIPsCount;
                    }
                }
            }
            return uniqueIPsCount;
        }).reduce(Long::sum);
        return result.orElse(0L);
    }

}
