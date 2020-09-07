package com.marufeb;

import java.util.Random;

public class Processo extends Thread {

    private final Random generator = new Random(System.nanoTime());
    public final int pagesNumber;
    private final MMU mmu;
    private int id = 0;

    public Processo(ThreadGroup group, String name, MMU instance) {
        super(group, name);
        pagesNumber = generator.nextInt(10)+1; // Max available: 16 pages per Processo
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
            try {
                mmu.join(generator.nextInt(4095),generator.nextInt(pagesNumber)+1, this);
                Thread.sleep(generator.nextInt(10)+5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
