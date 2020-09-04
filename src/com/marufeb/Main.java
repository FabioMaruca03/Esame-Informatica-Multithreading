package com.marufeb;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IllegalAccessException, InterruptedException {
        MMU mmu = new MMU(10, 4096*2, 254).start();
        Thread.sleep(5000);
        MMU.hasFinish = true;
        Arrays.stream(mmu.getPhysicalMemory()).forEach(System.out::println);
    }
}
