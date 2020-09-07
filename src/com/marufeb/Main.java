package com.marufeb;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        boolean done = false;
        Scanner scanner = new Scanner(System.in);
        while (!done) {
            try {
                System.out.print("\nHi, here's the MMU! Insert these values: \n Accessi Totali: ");
                int accesses = Integer.parseInt(scanner.next());
                System.out.print("Memoria Fisica: ");
                int memory = Integer.parseInt(scanner.next());
                System.out.print("N° Processi: ");
                int threads = Integer.parseInt(scanner.next());
                // System.out.print("Executions: ");
                int executions = 1;
                // if (executions == 0) executions = 1;

                //----| Enable for debug
                //MMU.enableAdvancedAnalytics();

                for (int i = 0; i < executions; i++) {
                    MMU mmu = new MMU(accesses, memory, threads).start();

                    System.out.println("EXECUTION No: " + i + "\n");
                    System.out.println("\n" + mmu + "\n");

                    // MMU.initialize();
                }

                System.out.println("\nDo you want to do it again? [Y/y char to confirm, any other one to end]\n");
                String temp = scanner.next();

                done = !temp.equals("Y") && !temp.equals("y");

            } catch (IllegalArgumentException e) {
                if (e instanceof NumberFormatException)
                    System.out.println("Only numbers are allowed!");
                else System.out.println(e.getMessage());
            }
        }
        scanner.close();
    }
}
