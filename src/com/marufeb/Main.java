package com.marufeb;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // Scanner for command line input
        Scanner scanner = new Scanner(System.in);
            try {
                //-----| Inputs
                System.out.print("\nHi, here's the MMU! Insert these values: \n Accessi Totali: ");
                int accesses = Integer.parseInt(scanner.next());
                System.out.print("Memoria Fisica (pages number): ");
                int memory = Integer.parseInt(scanner.next()) * 4;
                System.out.print("N° Processi: ");
                int threads = Integer.parseInt(scanner.next());
                int executions = 1;

                for (int i = 0; i < executions; i++) {

                    // Creating new MMU, arguments from the documentation
                    MMU mmu = new MMU(accesses, memory, threads).start();
                    MMU.enableAdvancedAnalytics();
                    // Printing analytics
                    System.out.println("EXECUTION No: " + i + "\n");
                    System.out.println("\n" + mmu + "\n");

                }

                System.out.println("Simulation finished");

            } catch (IllegalArgumentException e) {
                if (e instanceof NumberFormatException) // If not a number somewhere
                    System.out.println("Only numbers are allowed!");
                else System.out.println(e.getMessage());
            } catch (InterruptedException e) {
                e.getMessage();
            }
        scanner.close();
    }
}
