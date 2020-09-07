package com.marufeb;

import java.util.ArrayList;
import java.util.Random;

public class Processo extends Thread {

    private final Random generator = new Random(System.nanoTime());
    public final int pagesNumber;
    private final MMU mmu;
    private int id = 0;

    private final ArrayList<Integer> analytics = new ArrayList<>();

    public Processo(ThreadGroup group, String name, MMU instance) {
        super(group, name);
        pagesNumber = generator.nextInt(9) + 1; // Max available: 16 pages per Processo
        mmu = instance;
        for (int i = 0; i < 4; i++) {
            analytics.add(0);
        }
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
                ArrayList<Integer> availableLocations = new ArrayList<>();
                for (int location : mmu.getIdMapper().get(id))
                    if (mmu.getPageMapper().containsKey(location))
                        availableLocations.add(mmu.getPageMapper().get(location));
                ArrayList<Integer> result;
                if (availableLocations.size() == 0)
                    result = mmu.join(generator.nextInt(4095), this);
                else result = mmu.join(availableLocations.get(generator.nextInt(availableLocations.size())), this);

                for (int i = 0; i < result.size(); i++) {
                    analytics.set(i, analytics.get(i) + result.get(i));
                }

                Thread.sleep(generator.nextInt(10) + 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public String toString() {
        return "\nProcess id: " + id + "\nTotal accesses: " + analytics.get(0) + " pH: " + analytics.get(1) +
                " pF: " + analytics.get(2) + " replacements: " + analytics.get(3) + "\n\n";
    }
}
