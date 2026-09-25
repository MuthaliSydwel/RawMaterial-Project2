/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.mavenproject2;

/**
 *
 * @author callm
 */
import java.time.LocalDate;
import java.util.Random;

public class Demo {

    private Demo() {
    }

    // ------------------------------------------------ data structure demonstration
    public static void demonstrateDoublyLinkedList() {
        System.out.println("\n=== DOUBLY LINKED LIST DEMONSTRATION ===");
        DoublyLinkedList<String> list = new DoublyLinkedList<>();

        System.out.println("\n1) addLast (O(1)): Cement, Steel, Copper");
        list.addLast("Cement");
        list.addLast("Steel");
        list.addLast("Copper");
        show(list);

        System.out.println("\n2) addFirst (O(1)): Sand");
        list.addFirst("Sand");
        show(list);

        System.out.println("\n3) insertAt(2, \"Paint\") (O(n)):");
        list.insertAt(2, "Paint");
        show(list);

        System.out.println("\n4) get(3) (O(n), starts from nearest end): " + list.get(3));

        System.out.println("\n5) removeFirst (O(1)) -> " + list.removeFirst());
        show(list);

        System.out.println("\n6) removeLast (O(1)) -> " + list.removeLast());
        show(list);

        System.out.println("\n7) removeAt(1) (O(n) to reach, O(1) to unlink) -> " + list.removeAt(1));
        show(list);

        System.out.println("\nSize = " + list.size() + ", isEmpty = " + list.isEmpty());
        System.out.println("Each node stores: data, prev pointer, next pointer.");
        System.out.println("Because of the prev pointer we can traverse backwards and unlink a node without re-scanning.");
    }

    private static void show(DoublyLinkedList<String> list) {
        StringBuilder fwd = new StringBuilder("  head <-> ");
        list.forEachForward(s -> fwd.append(s).append(" <-> "));
        fwd.append("tail");
        StringBuilder bwd = new StringBuilder("  tail <-> ");
        list.forEachBackward(s -> bwd.append(s).append(" <-> "));
        bwd.append("head");
        System.out.println("  Forward : " + fwd.substring(2));
        System.out.println("  Backward: " + bwd.substring(2));
    }

    // ------------------------------------------------ performance benchmark
    public static void runBenchmark() {
        System.out.println("\n=== PERFORMANCE UNDER DIFFERENT INPUT CONDITIONS ===");
        int[] sizes = {100, 1000, 10000};

        System.out.println("\nMERGE SORT (by quantity) - comparisons and time");
        System.out.printf("%-8s %-16s %14s %12s%n", "n", "Input order", "Comparisons", "Time (ms)");
        System.out.println("-".repeat(54));
        for (int n : sizes) {
            // 1) random order
            InventoryManager random = generate(n, 42);
            report(n, "Random", random);

            // 2) already sorted ascending (sort again)
            report(n, "Already sorted", random);

            // 3) reverse sorted
            random.sort(InventoryManager.SortField.QUANTITY, false);
            report(n, "Reverse sorted", random);
        }
        System.out.println("Observation: comparisons never exceed about n*log2(n), whatever the input order.");
        System.out.println("Merge sort splits and merges at every level: best, average and worst time are O(n log n).");

        System.out.println("\nLINEAR SEARCH (by ID) - elements examined");
        System.out.printf("%-8s %12s %12s %12s %12s%n", "n", "Best(first)", "Middle", "Worst(last)", "Not found");
        System.out.println("-".repeat(60));
        for (int n : sizes) {
            InventoryManager inv = generate(n, 7);
            long best = probe(inv, "RM00000");
            long middle = probe(inv, String.format("RM%05d", n / 2));
            long worst = probe(inv, String.format("RM%05d", n - 1));
            long none = probe(inv, "NOPE");
            System.out.printf("%-8d %12d %12d %12d %12d%n", n, best, middle, worst, none);
        }
        System.out.println("Best case: O(1); average and worst case: O(n). Middle is one lookup, not a measured average.");
    }

    private static void report(int n, String label, InventoryManager inv) {
        long start = System.nanoTime();
        long comps = inv.sort(InventoryManager.SortField.QUANTITY, true);
        double ms = (System.nanoTime() - start) / 1_000_000.0;
        System.out.printf("%-8d %-16s %14d %12.3f%n", n, label, comps, ms);
    }

    private static long probe(InventoryManager inv, String id) {
        inv.searchById(id);
        return inv.lastSearchComparisons();
    }

    /**
     * Builds an inventory of n random materials with IDs RM00000 ... RM(n-1) in
     * order.
     */
    private static InventoryManager generate(int n, long seed) {
        Random rnd = new Random(seed);
        InventoryManager inv = new InventoryManager();
        LocalDate base = LocalDate.of(2026, 1, 1);
        for (int i = 0; i < n; i++) {
            inv.addMaterialFast(new Material(String.format("RM%05d", i), "Material " + i, "Cat" + (i % 5),
                    "kg", "Supplier" + (i % 8), rnd.nextInt(5000), 100, 1 + rnd.nextInt(500) / 2.0,
                    base.plusDays(rnd.nextInt(300))));
        }
        return inv;
    }
}
