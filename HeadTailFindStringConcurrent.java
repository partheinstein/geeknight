package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.time.Instant;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws Exception {

        // Validate input
        if (args.length != 2)            throw new IllegalArgumentException("Needle (search term) and haystack (file path) not provided ");
        if (args[0].length() != 10)      throw new IllegalArgumentException("Needle must be 10 characters");
        if (!new File(args[1]).exists()) throw new FileNotFoundException("File containing Haystack cannot be found");

        final File file = new File(args[1]);
        final byte[] needle = args[0].getBytes("UTF-8");

        try(FileInputStream fis = new FileInputStream(file)) {
            FileChannel fc = fis.getChannel();

            final short bufferLen = 3000; // How much data do we read on each iteration
            int currentPosition = 0; // Current read position within file
            short bytesRead; // How much data has been read on current iteration

            ExecutorService es = Executors.newFixedThreadPool(3);
            Semaphore s1 = new Semaphore(1);
            //CountDownLatch cdl = new CountDownLatch(35071);
            CountDownLatch cdl = new CountDownLatch(3508);

            long start = Instant.now().toEpochMilli();
            do {
                ByteBuffer buffer = ByteBuffer.allocate(bufferLen);
                bytesRead = (short) fc.read(buffer, currentPosition); // Read data at current position from file

                es.submit(new Farmer(buffer.array(), needle, bytesRead, s1, start, cdl));

                // In case the match is within the last 10 characters of the bytes  read them as part of the next pass
                currentPosition = currentPosition + bytesRead - 10;
            } while (bytesRead > 10 && s1.availablePermits() == 1);


            cdl.await();
            es.shutdownNow();

            if(s1.availablePermits() == 1) {
                System.out.print("NOT Found! - ");
                System.out.println(Instant.now().toEpochMilli() - start);
            } else {
                es.shutdownNow();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
