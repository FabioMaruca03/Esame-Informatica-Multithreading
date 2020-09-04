package com.marufeb;

import java.util.*;

public class MMU {

    public static boolean hasFinish = false;
    static int pageHit = 0;
    static int pageFaults = 0;

    private final int accessiTotali;
    private final int memFisica;
    private final int n;

    private final ThreadGroup processGroup = new ThreadGroup("Processes");
    private final Map<Integer, ArrayList<Integer>> pageMapper = new HashMap<Integer, ArrayList<Integer>>();
    private final ArrayList<Processo> processList = new ArrayList();
    private final Byte[] virtualMemory;
    private final Byte[] physicalMemory;

    public MMU(int accessiTotali, int memFisica, int n) throws IllegalArgumentException {
        this.accessiTotali = accessiTotali;
        if (memFisica%4 == 0) {
            this.memFisica = memFisica;
            this.physicalMemory = new Byte[memFisica];
        } else throw new IllegalArgumentException("memFisica is not divisible by page size!");
        if (n > 0 && n < 255) {
            this.n = n;
        } else throw new IllegalArgumentException("Number of threads could be only greater than 0 and less than 256");
        this.virtualMemory = new Byte[4096];

        for (int i = 0; i < 4096; i++) {
            virtualMemory[i] = (byte) 0;
        }

        for (int i = 0; i < memFisica; i++) {
            physicalMemory[i] = (byte) 0;
        }

        initProcess();
    }

    /**
     * Default call for every Processo object
     */
    public boolean interrupt(int indexToAccess) {
        // todo
        return false;
    }

    /***
     * Initialize the entire process stack
     */
    private void initProcess() {
        for (int i = 0; i < n; i++) {
            Processo process = new Processo(processGroup, "Processo "+i, this).assignId((byte)i+1);
            allocateProcess(process); // Physical memory allocation
            processList.add(process);
        }
    }

    /**
     * Allocates processes in physical memory by their number of pages
     */
    private void allocateProcess(Processo process) {
        ArrayList<Integer> allocations = new ArrayList<>();
        for (int i = 0; i <= memFisica-4 && allocations.size() < process.pagesNumber; i+=4) {
            int memSum = 0;
            for (int j = i; j < i+4; j++) {
                memSum += physicalMemory[j].intValue();
            }

            if (memSum == 0) { // Free memory (4(FREE_MEMORY_VALUE) = 0)
                allocations.add(i);
                for (int j = i; j < i+4; j++) {
                    physicalMemory[j] = (byte) process.getRelatedId(); // Set page value
                }
            }
        }
        if (allocations.size() < process.pagesNumber)
            throw new IllegalStateException("Cannot allocate process: "+process.getName());
        else pageMapper.put(process.getRelatedId(), allocations);
    }

    /**
     * Deletes the process from virtual and physical memory
     */
    public void delProcess(int id) {
        if (!processList.removeIf(it->it.getRelatedId() == id))
            throw new IllegalArgumentException("No such process with id: "+id);
        else {
            ArrayList<Integer> keys = new ArrayList<>();
            pageMapper.keySet().stream()
                    .filter(it -> it == id)
                    .forEach(it -> {
                        keys.add(it);
                        for (int i = 0; i < 4; i++) { // Clear physical memory
                            physicalMemory[i] = (byte) 0;
                        }
                    });
            keys.forEach(pageMapper::remove); // Remove mapping via reflection

            // todo: Remove from the virtual memory also
        }
    }

    /**
     * Starts every Processo object
     * @return MMU instance (for methods chaining)
     */
    private MMU start() {
        processList.forEach(Thread::start);
        return this;
    }

    /*-----| Getters and Setters |-----*/

    public int getAccessiTotali() {
        return accessiTotali;
    }

    public int getMemFisica() {
        return memFisica;
    }

    public int getN() {
        return n;
    }

    public ArrayList<Processo> getProcessList() {
        return processList;
    }

    public static void main(String[] args) throws IllegalAccessException, InterruptedException {
        MMU mmu = new MMU(10, 4096*2, 254).start();
        Thread.sleep(5000);
        MMU.hasFinish = true;
        Arrays.stream(mmu.physicalMemory).forEach(System.out::println);
    }
}
