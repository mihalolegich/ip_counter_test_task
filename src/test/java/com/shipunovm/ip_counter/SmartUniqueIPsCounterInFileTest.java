package com.shipunovm.ip_counter;

import com.shipunovm.ip_counter.smartcounter.SmartUniqueIPsCounterInFile;
import com.shipunovm.ip_counter.trivialcounter.TrivialUniqueIPsCounterInFile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SmartUniqueIPsCounterInFileTest {

    public static final String FILE_NAME = "testfile.txt";
    public static final int MAX_AMNT = 256;

    @BeforeAll
    static void createFile() throws IOException {
        File testFile = new File(FILE_NAME);
        if (!testFile.exists()) {
            System.out.println("creating test file named " + FILE_NAME);
            new TestFileCreator().create();
        }
    }

    @Test
    void compareUniqueIPsFileCounters() {
        long startTime = currentTimeMillis();
        long smartWayResult = new SmartUniqueIPsCounterInFile(FILE_NAME).countUniqueIPsInFile();
        long endTime = currentTimeMillis();
        long smartWayTime = endTime - startTime;

        startTime = currentTimeMillis();
        long trivialWayResult = new TrivialUniqueIPsCounterInFile(FILE_NAME).countUniqueIPsInFile();
        endTime = currentTimeMillis();
        long trivialWayTime = endTime - startTime;

        System.out.println("trivial method time is " + trivialWayTime);
        System.out.println("smart method time is " + smartWayTime);
        assertEquals(trivialWayResult, smartWayResult);
        assertTrue(trivialWayTime > smartWayTime);
    }

    @Test
    void compareForkJoinToTrivialCounting() {
        boolean[][][][] arr = new boolean[MAX_AMNT][MAX_AMNT][MAX_AMNT][MAX_AMNT];
        arr[MAX_AMNT / 2][MAX_AMNT / 2][MAX_AMNT / 2][MAX_AMNT / 2] = true;

        long startTime = currentTimeMillis();
        long forkJoinResult = new SmartUniqueIPsCounterInFile(FILE_NAME).getUniqueIPsCount(arr);
        long endTime = currentTimeMillis();
        long forkJoinTime = endTime - startTime;

        startTime = currentTimeMillis();
        long trivialWayResult = new TrivialUniqueIPsCounterInFile(FILE_NAME).getUniqueIPsCount(arr);
        endTime = currentTimeMillis();
        long trivialWayTime = endTime - startTime;

        System.out.println("trivial counting time is " + trivialWayTime);
        System.out.println("forkjoin counting time is " + forkJoinTime);
        assertEquals(trivialWayResult, forkJoinResult);
        assertTrue(trivialWayTime > forkJoinTime);
    }

}