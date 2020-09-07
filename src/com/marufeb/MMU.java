package com.marufeb;

import java.util.*;
import java.util.concurrent.Semaphore;

public class MMU {

    public static boolean hasFinish = false;
    static int pageHit = 0;
    static int pageFaults = 0;
    static int accesses = 0;
    static int killCount = 0;
    private static boolean advancedMode = false;

    private final int accessiTotali;
    private final int memFisica; // RAM size
    private final int n; // Processes counter

    private final ThreadGroup processGroup = new ThreadGroup("Processes");
    private final Map<Integer, ArrayList<Integer>> idMapper = new HashMap<>(); // <ProcessID, Virtual locations>
    private final Map<Integer, Integer> pageMapper = new HashMap<>(); // <Virtual location, Physical location>
    private final ArrayList<Processo> processList = new ArrayList();
    private final Queue<Processo> processQueue = new ArrayDeque<>();
    private final Byte[] virtualMemory; // SWAP
    private final Byte[] physicalMemory; // RAM
    private final Byte[] blankPage = {0, 0, 0, 0};

    static final Semaphore isWorking = new Semaphore(1);
    public final Semaphore look = new Semaphore(1);

    public MMU(int accessiTotali, int memFisica, int n) throws IllegalArgumentException {
        this.accessiTotali = accessiTotali;
        if (memFisica%4 == 0) {
            this.memFisica = memFisica;
            this.physicalMemory = new Byte[memFisica];
        } else throw new IllegalArgumentException("memFisica is not divisible by page size!");
        if (n >= 0 && n < 127) { // Limitation (8 bit positive only)
            this.n = n;
        } else throw new IllegalArgumentException("Number of threads could be only greater than 0 and less than 127");
        this.virtualMemory = new Byte[4096];

        for (int i = 0; i < 4096; i++) {
            virtualMemory[i] = 0;
        }

        for (int i = 0; i < memFisica; i++) {
            physicalMemory[i] = 0;
        }

        initProcess();
    }

    public static void enableAdvancedAnalytics() {
        advancedMode = true;
    }

    /**
     * Default call for every Processo object
     *
     * @param indexToAccess The actual index to access ( included the offset )
     * @param process       The process that needs to access the page
     */
    public ArrayList<Integer> join(int indexToAccess, Processo process) {
        ArrayList<Integer> DEFAULT = new ArrayList<>();
        for (int i = 0; i < 4; i++) DEFAULT.add(0);

        try {
            isWorking.acquire();

            if (accessiTotali == accesses) {
                hasFinish = true;
                isWorking.release(n);
                look.release();
                return DEFAULT;
            } else if (hasFinish) return DEFAULT;

            else {
                accesses++; // Increment the accesses number
                DEFAULT.set(0, 1);
            }

            int id = process.getRelatedId();

            if (pageMapper.containsKey(idMapper.get(id).get(0)) && pageMapper.containsValue(indexToAccess)) { // If the page is mapped
                pageHit++;
                DEFAULT.set(1, 1);
            } else {
                pageFaults++;
                DEFAULT.set(2, 1);
                int pagesAvailable = memFisica / 4 - pageMapper.size();
                while (pagesAvailable < process.pagesNumber) { // Swap out
                    pagesAvailable += killProcess(processQueue.poll().getRelatedId()).size();
                    DEFAULT.set(3, DEFAULT.get(3) + 1);
                    killCount++;
                }

                ArrayList<Integer> missingPages = idMapper.get(id);
                Byte[] page = {(byte) id, (byte) id, (byte) id, (byte) id};

                for (int i = 0, j = 0; i < memFisica && j < missingPages.size(); i += 4) { // Swap in
                    Byte[] slice = Arrays.copyOfRange(physicalMemory, i, i + 4);
                    if (Arrays.compare(slice, blankPage) == 0) { // Free memory
                        System.arraycopy(page, 0, physicalMemory, i, 4);
                        pageMapper.put(missingPages.get(j), i);
                        j++;
                    }
                }
                processQueue.add(process);
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            isWorking.release();
        }

        return DEFAULT;
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
     * Allocates processes in virtual memory by their number of pages
     */
    private void allocateProcess(Processo process) {
        ArrayList<Integer> allocations = new ArrayList<>();
        for (int i = 0; i < 4096 && allocations.size() <= process.pagesNumber; i+=4) {
            Byte[] slice = Arrays.copyOfRange(virtualMemory, i, i + 4);
            if (Arrays.compare(slice, blankPage) == 0) { // Free memory
                allocations.add(i);
                for (int j = i; j < i + 4; j++) {
                    virtualMemory[j] = (byte) process.getRelatedId(); // Set page value
                }
            }
        }
        if (allocations.size() < process.pagesNumber)
            throw new IllegalStateException("Cannot allocate process: " + process.getName());
        else idMapper.put(process.getRelatedId(), allocations);
        if (advancedMode)
            System.out.println("Allocated " + allocations.size() + " pages for process " + process.getRelatedId());
    }

    /**
     * Removes page mapping and deletes it from the physical memory
     */
    private ArrayList<Integer> killProcess(int id) {
        for (Integer pageLocation : idMapper.get(id)) { // For each location
            if (pageMapper.containsKey(pageLocation)) { // If bind is present
                System.arraycopy(blankPage, 0, physicalMemory, pageMapper.get(pageLocation), 4);
                pageMapper.remove(pageLocation);
                if (advancedMode)
                    System.out.println("Process: " + id + " killed");
            }
        }
        return idMapper.get(id);
    }

    /**
     * Starts every Processo object
     * @return MMU instance (for methods chaining)
     */
    MMU start() throws InterruptedException {
        for (Processo p : processList) p.start();
        look.acquire();
        return this;
    }

    /*-----| Getters and Setters |-----*/

    public int getAccessiTotali() {
        return accessiTotali;
    }

    public int getMemFisica() {
        return memFisica;
    }

    public Byte[] getPhysicalMemory() {
        return physicalMemory;
    }

    public Byte[] getVirtualMemory() {
        return virtualMemory;
    }

    public Map<Integer, Integer> getPageMapper() {
        return pageMapper;
    }

    public Map<Integer, ArrayList<Integer>> getIdMapper() {
        return idMapper;
    }

    public int getN() {
        return n;
    }

    public ArrayList<Processo> getProcessList() {
        return processList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("MMU Analytics\n");

        if (advancedMode) {
            builder.append("\nID Mapper: \n");
            for (Integer key : getIdMapper().keySet()) {
                builder.append(key).append("\t").append(getIdMapper().get(key)).append("\n");
            }

            builder.append("\nPage Mapper: \n");
            for (Integer key : getPageMapper().keySet()) {
                builder.append(key).append("\t").append(getPageMapper().get(key)).append("\n");
            }
        }

        builder.append("Total accesses: ").append(MMU.accesses).append(" pH ").append(MMU.pageHit)
                .append(" pF ").append(MMU.pageFaults).append(" kills ").append(killCount).append("\n");

        builder.append("\nProcesses Analytics\n");
        for (Processo temp : processList)
            builder.append(temp);

        return builder.toString();
    }

    public static void initialize() {
        hasFinish = false;
        killCount = 0;
        pageFaults = 0;
        pageHit = 0;
        accesses = 0;
    }
}
