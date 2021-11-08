package com.shipunovm.ip_counter.smartcounter.filereader;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsynchronousFileReader {

    public static final int MAX_IP_LENGTH_WITH_LINE_BREAK = 17;
    public static final char LINE_BREAK_CHAR = '\n';

    private final AsynchronousFileChannel fileChannel;
    private final ByteBuffer buffer;

    public AsynchronousFileReader(AsynchronousFileChannel fileChannel, ByteBuffer buffer) {
        this.fileChannel = fileChannel;
        this.buffer = buffer;
    }

    public FileReadDataSummary getBytesAndLastReadBytePosition(long positionInFile) {
        try {
            Future<Integer> operation = fileChannel.read(buffer, positionInFile);
            operation.get();
            buffer.flip();
            byte[] data = new byte[buffer.limit()];
            buffer.get(data);
            int finalBufferSize = getLastLinebreakEndPosition(data);
            buffer.clear();
            return new FileReadDataSummary(finalBufferSize, data);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        throw new AsynchronousFileReaderException("couldn't get bytes and final read position");
    }

    private int getLastLinebreakEndPosition(byte[] bytes) {
        int firstOctetOfPossibleIpPosition = bytes.length - MAX_IP_LENGTH_WITH_LINE_BREAK;
        if (firstOctetOfPossibleIpPosition < 0) {
            return bytes.length;
        }
        for (int i = bytes.length - 1; i > firstOctetOfPossibleIpPosition; i--) {
            if (LINE_BREAK_CHAR == bytes[i]) {
                return i;
            }
        }
        throw new AsynchronousFileReaderException("not found line break in last IP address!!!");
    }

}
