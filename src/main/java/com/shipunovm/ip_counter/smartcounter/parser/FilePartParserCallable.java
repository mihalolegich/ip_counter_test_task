package com.shipunovm.ip_counter.smartcounter.parser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import static java.lang.Integer.parseInt;

public class FilePartParserCallable implements Callable<Boolean> {

    private final boolean[][][][] finalArray;
    private final byte[] bytes;

    public FilePartParserCallable(boolean[][][][] finalArray, byte[] bytes) {
        this.finalArray = finalArray;
        this.bytes = bytes;
    }

    @Override
    public Boolean call() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)))) {
            String[] octets;
            for (String tempStr = reader.readLine(); tempStr != null && tempStr.length() > 1; tempStr = reader.readLine()) {
                octets = tempStr.split("\\.");
                if (octets.length > 3) {
                    finalArray[parseInt(octets[0])][parseInt(octets[1])][parseInt(octets[2])][parseInt(octets[3])] = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}
