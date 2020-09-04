package com.marufeb;

import java.util.Random;

public class Processo extends Thread {

    private final Random generator = new Random(System.currentTimeMillis());
    public final int pagesNumber;
    private final MMU mmu;
    private int id = 0;

    public Processo(ThreadGroup group, String name, MMU instance) {
        super(group, name);
        pagesNumber = generator.nextInt(10);
        mmu = instance;
    }

    public Processo assignId (int id) {
        this.id = id;
        return this;
    }

    public int getRelatedId() {
        return id;
    }

    @Override
    public void run() {
        while (!MMU.hasFinish) {
            mmu.interrupt(generator.nextInt(4095), this);
            try {
                Thread.sleep(generator.nextInt(10)+5);
            } catch (InterruptedException e) {
                System.out.println("Process: "+id+" ended in waiting status");
            }
        }
    }

}
