package com.loggle.rpc.sea.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author guomy
 * @create 2016-08-31 10:39.
 */
public class MapMemeryBuffer {
    public static void main(String[] args) throws Exception {
        //ByteBuffer byteBuf = ByteBuffer.allocate(1024 * 14 * 1024);
        byte[] bbb = new byte[14 * 1024 * 1024];
        FileInputStream fis = new FileInputStream("e://CentOS-6.6-x86_64-bin-DVD2.iso");
        FileOutputStream fos = new FileOutputStream("e://CentOS-6.6-x86_64-bin-DVD2.iso.back");
        FileChannel fc = fis.getChannel();
        ByteBuffer byteBuf = ByteBuffer.allocate((int)fc.size());
        long timeStar = System.currentTimeMillis();// 得到当前的时间

        //fc.read(byteBuf);// 1 读取
        MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        System.out.println(fc.size()/1024);
        long timeEnd = System.currentTimeMillis();// 得到当前的时间
        System.out.println("Read time :" + (timeEnd - timeStar) + "ms");

        //bbb = byteBuf.array();
        timeStar = System.currentTimeMillis();
        //fos.write(bbb);//2.写入
        mbb.flip();
        timeEnd = System.currentTimeMillis();
        System.out.println("Write time :" + (timeEnd - timeStar) + "ms");
        fos.flush();
        fc.close();
        fis.close();
    }
}
