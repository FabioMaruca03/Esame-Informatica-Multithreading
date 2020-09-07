package com.marufeb;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        MMU mmu = new MMU(100, 50*4, 100).start();

        mmu.look.acquire();

        System.out.println("\nPhysical memory: ");
        for (int i = 0; i < mmu.getMemFisica(); i+=4) {
            System.out.print("Page: "+i/4+":\t");
            for (int j = 0; j < 4; j++) {
                System.out.print(mmu.getPhysicalMemory()[j] + " ");
            }
            System.out.println();
        }

        System.out.println("\nVirtual memory: ");
        for (int i = 0; i < 4096; i+=4) {
            System.out.print("Page: "+i/4+":\t");
            for (int j = 0; j < 4; j++) {
                System.out.print(mmu.getVirtualMemory()[j] + " ");
            }
            System.out.println();
        }

        System.out.println("\nID Mapper");
        for (Integer key : mmu.getIdMapper().keySet()){
            System.out.println(key + "\t" + mmu.getIdMapper().get(key));
        }

        System.out.println("\nPage Mapper");
        for (Integer key : mmu.getPageMapper().keySet()){
            System.out.println(key + "\t" + mmu.getPageMapper().get(key));
        }

        System.out.println("Accessi totali: " + MMU.accesses + " N: " + mmu.getN()
                + " pH " + MMU.pageHit + " pF " + MMU.pageFaults);
    }
}
