package com.shipunovm.ip_counter.smartcounter.filereader;

public final class FileReadDataSummary {

    private final int lastReadByteNumberFromBuffer;
    private final byte[] bytes;

    public FileReadDataSummary(int lastReadByteNumberFromBufferInFile, byte[] bytes) {
        this.lastReadByteNumberFromBuffer = lastReadByteNumberFromBufferInFile;
        this.bytes = bytes;
    }

    public int getLastReadBytePosition() {
        return lastReadByteNumberFromBuffer;
    }

    public byte[] getBytes() {
        return bytes;
    }

}
