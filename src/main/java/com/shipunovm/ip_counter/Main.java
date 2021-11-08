package com.shipunovm.ip_counter;

import com.shipunovm.ip_counter.smartcounter.SmartUniqueIPsCounterInFile;

import java.io.IOException;
import java.util.Properties;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;

public class Main {

    public static void main(String[] args) {
        long startTime = currentTimeMillis();
        out.println("Amount of unique IP addresses in file is " +
                new SmartUniqueIPsCounterInFile(getFileName()).countUniqueIPsInFile());
        long endTime = currentTimeMillis();
        out.println("smart method time is " + (endTime - startTime));
    }

    private static String getFileName() {
        try {
            Properties prop = new Properties();
            prop.load(Main.class.getResourceAsStream("/config.properties"));
            return prop.getProperty("absolute.file.path");
        } catch (IOException e) {
            throw new RuntimeException("couldn't read config file!!!", e);
        }
    }

}
