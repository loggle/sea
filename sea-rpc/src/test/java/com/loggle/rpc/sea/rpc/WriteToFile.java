package com.loggle.rpc.sea.rpc;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * @author guomy
 * @create 2016-08-31 14:50.
 */
public class WriteToFile {

    private String path;
    private FileChannel fileChannel;

    public WriteToFile(String path) {
        this.path = path;
    }

    public void init() throws FileNotFoundException {
        File file = new File(path);
        RandomAccessFile raf = new RandomAccessFile(file,"rw");
        fileChannel = raf.getChannel();
    }

    public void write(String str) throws IOException {
        ByteArrayInputStream bais = null;
        ReadableByteChannel readableByteChannel = null;
        try {
            bais = new ByteArrayInputStream(str.getBytes());
            readableByteChannel = Channels.newChannel(bais);

            fileChannel.transferFrom(readableByteChannel, fileChannel.size(), str.getBytes().length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bais != null) bais.close();
            if(readableByteChannel != null) readableByteChannel.close();
        }
    }

    public void close() throws IOException {
        fileChannel.close();
    }


}
