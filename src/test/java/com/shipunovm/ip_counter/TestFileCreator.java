package com.shipunovm.ip_counter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TestFileCreator {

    public static final int MAX_OCTET_VALUE = 254;
    public static final int MIN_OCTET_VALUE = 1;
    public static final String LINE_BREAK = "\n";
    public static final int IP_AMNT = 10_000_000;

    public void create() throws IOException {
        File file = new File("testfile.txt");
        file.delete();
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        Random random = new Random();
        Set<String> uniqueStrings = new HashSet<>();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < IP_AMNT; i++) {
            sb.append(random.nextInt(MAX_OCTET_VALUE - MIN_OCTET_VALUE) + MIN_OCTET_VALUE);
            sb.append('.');
            sb.append(random.nextInt(MAX_OCTET_VALUE - MIN_OCTET_VALUE) + MIN_OCTET_VALUE);
            sb.append('.');
            sb.append(random.nextInt(MAX_OCTET_VALUE - MIN_OCTET_VALUE) + MIN_OCTET_VALUE);
            sb.append('.');
            sb.append(random.nextInt(MAX_OCTET_VALUE - MIN_OCTET_VALUE) + MIN_OCTET_VALUE);
            sb.append(LINE_BREAK);
            final String str = sb.toString();
            sb.delete(0, sb.length());
            writer.write(str);
            uniqueStrings.add(str);
        }
        writer.close();
        File testFileStringsCount = new File("testFileStringsCount.txt");
        testFileStringsCount.delete();
        testFileStringsCount.createNewFile();
        writer = new FileWriter(testFileStringsCount);
        writer.write("unique Strings amount in file is " + uniqueStrings.size());
        writer.close();
    }

}
