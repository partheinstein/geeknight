package com.company;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class Farmer implements Runnable {

    private byte[] haystack, needle;
    private short lastByte;
    private Semaphore s1;
    private long start;
    private CountDownLatch cdl;

    public Farmer(
            final byte[] haystack,
            final byte[] needle,
            final short lastByte,
            Semaphore s1,
            long start,
            CountDownLatch cdl
    ) {
        this.haystack = haystack;
        this.needle = needle;
        this.lastByte = lastByte;
        this.s1 = s1;
        this.start = start;
        this.cdl = cdl;
    }

    @Override
    public void run() {
        for(int i = 9; i < lastByte; i++) {
            if(haystack[i-9] == needle[0] && haystack[i] == needle[9]) { // Test head and tail characters
                if (haystack[i - 4] == needle[5] && haystack[i - 5] == needle[4]) { // Test middle two characters
                    if (haystack[i - 2] == needle[7] && haystack[i - 3] == needle[6] && haystack[i - 8] == needle[1]) { // test remainder
                        System.out.print("Found! - ");
                        System.out.println(Instant.now().toEpochMilli() - start);
                        s1.tryAcquire();
                        cdl.countDown();
                        return;
                    }
                }
            }
        }
        cdl.countDown();
    }
}
