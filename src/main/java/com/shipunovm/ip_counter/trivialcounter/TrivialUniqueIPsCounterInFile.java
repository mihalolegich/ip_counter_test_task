package com.shipunovm.ip_counter.trivialcounter;

import com.shipunovm.ip_counter.UniqueIPsCounterInFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static java.lang.Integer.parseInt;

public class TrivialUniqueIPsCounterInFile extends UniqueIPsCounterInFile {

    private final String fileName;
    private static final int MAX_OCTET_VAL = 256;

    public TrivialUniqueIPsCounterInFile(String fileName) {
        this.fileName = fileName;
    }

    public long countUniqueIPsInFile() {
        boolean[][][][] finalArray = new boolean[MAX_OCTET_VAL][MAX_OCTET_VAL][MAX_OCTET_VAL][MAX_OCTET_VAL];
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            String[] octets;
            while ((line = br.readLine()) != null) {
                octets = line.split("\\.");
                finalArray[parseInt(octets[0])][parseInt(octets[1])][parseInt(octets[2])][parseInt(octets[3])] = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getUniqueIPsCount(finalArray);
    }

    public long getUniqueIPsCount(boolean[][][][] arr) {
        int uniqueIPsCount = 0;
        for (int i = 0; i < MAX_OCTET_VAL; i++) {
            for (int j = 0; j < MAX_OCTET_VAL; j++) {
                for (int k = 0; k < MAX_OCTET_VAL; k++) {
                    for (int l = 0; l < MAX_OCTET_VAL; l++) {
                        uniqueIPsCount = arr[i][j][k][l] ? uniqueIPsCount + 1 : uniqueIPsCount;
                    }
                }
            }
        }
        return uniqueIPsCount;
    }

}
