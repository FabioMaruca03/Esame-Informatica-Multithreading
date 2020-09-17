package com.marufeb;

import java.util.ArrayList;
import java.util.Random;

public class Processo extends Thread {

    private final Random generator = new Random(System.nanoTime()); // New generator
    public final int pagesNumber; // The actual pages number
    private final MMU mmu; // The MMU object that creates
    private int id = 0; // The id of the process

    private final ArrayList<Integer> analytics = new ArrayList<>(); // Analytics

    public Processo(ThreadGroup group, String name, MMU instance) {
        super(group, name); // Init thread
        setDaemon(true);
        pagesNumber = generator.nextInt(9) + 1; // Max available: 16 pages per Processo
        mmu = instance; // Update the instance
        for (int i = 0; i < 4; i++) {
            analytics.add(0); // INit the analytics
        }
    }

    public Processo assignId (int id) {
        this.id = id;
        return this; // Chaining
    }

    public int getRelatedId() {
        return id;
    }

    @Override
    public void run() {
        while (!MMU.hasFinish) { // Auto deletes the thread
            try {
                ArrayList<Integer> availableLocations = new ArrayList<>(); // Pages locations
                for (int location : mmu.getIdMapper().get(id)) // For each location mapped in physical memory by id
                    if (mmu.getPageMapper().containsKey(location)) // If is mapped
                        availableLocations.add(mmu.getPageMapper().get(location)); // Add to available locations
                ArrayList<Integer> result; // The result of join method
                if (availableLocations.size() == 0)
                    result = mmu.join(generator.nextInt(4095), this); // Join with a random location
                else result = mmu.join(availableLocations.get(generator.nextInt(availableLocations.size())), this); // Join based on the available locations

                for (int i = 0; i < result.size(); i++) {
                    analytics.set(i, analytics.get(i) + result.get(i)); // Update analytics
                }

                Thread.sleep(generator.nextInt(10) + 5); // Sleep
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public String toString() { // Analytics
        return "\nProcess id: " + id + "\nTotal accesses: " + analytics.get(0) + " pH: " + analytics.get(1) +
                " pF: " + analytics.get(2) + " replacements: " + analytics.get(3) + "\n\n";
    }
}
