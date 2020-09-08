package com.marufeb;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        boolean done = false;

        // Scanner for command line input
        Scanner scanner = new Scanner(System.in);
        while (!done) { // While implementation for do-again system
            try {
                //-----| Inputs
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

                // Default executions = 1
                for (int i = 0; i < executions; i++) {

                    // Creating new MMU, arguments from the documentation
                    MMU mmu = new MMU(accesses, memory, threads).start();

                    // Printing analytics
                    System.out.println("EXECUTION No: " + i + "\n");
                    System.out.println("\n" + mmu + "\n");

                    // MMU.initialize();
                }

                System.out.println("\nDo you want to do it again? [Y/y char to confirm, any other one to end]\n");

                // Updating input from console
                String temp = scanner.next();

                done = !temp.equals("Y") && !temp.equals("y");

            } catch (IllegalArgumentException e) {
                if (e instanceof NumberFormatException) // If not a number somewhere
                    System.out.println("Only numbers are allowed!");
                else System.out.println(e.getMessage());
            } catch (InterruptedException e) {
                e.getMessage();
            }
        }
        scanner.close();
    }
}
