/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.mavenproject2;

/**
 *
 * @author callm
 */
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {

    private static final Scanner in = new Scanner(System.in);
    private static final InventoryManager inventory = new InventoryManager();

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  RAW MATERIAL INVENTORY MANAGEMENT SYSTEM");
        System.out.println("==============================================");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ", 0, 12);
            System.out.println();
            switch (choice) {
                case 1 ->
                    addMaterial();
                case 2 ->
                    removeMaterial();
                case 3 ->
                    receiveStock();
                case 4 ->
                    issueStock();
                case 5 ->
                    searchMenu();
                case 6 ->
                    sortMenu();
                case 7 ->
                    inventory.display(true);
                case 8 ->
                    inventory.display(false);
                case 9 ->
                    lowStock();
                case 10 ->
                    Demo.demonstrateDoublyLinkedList();
                case 11 ->
                    Demo.runBenchmark();
                case 12 ->
                    loadSample();
                case 0 -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n---------------- MAIN MENU ----------------");
        System.out.println(" 1. Add raw material");
        System.out.println(" 2. Remove raw material");
        System.out.println(" 3. Receive stock (from supplier)");
        System.out.println(" 4. Issue stock to production");
        System.out.println(" 5. Search materials (linear search)");
        System.out.println(" 6. Sort materials (merge sort)");
        System.out.println(" 7. Display all (head -> tail)");
        System.out.println(" 8. Display all (tail -> head)");
        System.out.println(" 9. Low-stock (reorder) report");
        System.out.println("10. Demonstrate the doubly linked list");
        System.out.println("11. Performance benchmark (different inputs)");
        System.out.println("12. Load sample data");
        System.out.println(" 0. Exit");
        System.out.println("-------------------------------------------");
    }

    // ------------------------------------------------------------ menu actions
    private static void addMaterial() {
        System.out.println("--- Add Raw Material ---");
        String id = readNonEmpty("Material ID (e.g. RM001): ");
        String name = readNonEmpty("Name: ");
        String category = readNonEmpty("Category (Metal/Plastic/Chemical/Wood/...): ");
        String unit = readNonEmpty("Unit of measure (kg, litre, pcs...): ");
        String supplier = readNonEmpty("Supplier: ");
        int qty = readInt("Quantity in stock: ", 0, Integer.MAX_VALUE);
        int reorder = readInt("Reorder level: ", 0, Integer.MAX_VALUE);
        double cost = readDouble("Unit cost: ");
        LocalDate date = readDate("Date received (yyyy-mm-dd, blank = today): ");

        Material m = new Material(id, name, category, unit, supplier, qty, reorder, cost, date);
        int where = readInt("Insert at 1) end of list  2) front of list: ", 1, 2);
        boolean ok = (where == 1) ? inventory.addMaterial(m) : inventory.addMaterialAtFront(m);
        System.out.println(ok ? "Material added." : "A material with ID '" + id + "' already exists. Not added.");
    }

    private static void removeMaterial() {
        System.out.println("--- Remove Raw Material ---");
        String id = readNonEmpty("Material ID to remove: ");
        Material removed = inventory.removeMaterial(id);
        if (removed == null) {
            System.out.println("No material with ID '" + id + "' found.");
        } else {
            System.out.println("Removed: " + removed.getId() + " - " + removed.getName());
        }
        System.out.println("(elements examined by linear search: " + inventory.lastSearchComparisons() + ")");
    }

    private static void receiveStock() {
        System.out.println("--- Receive Stock ---");
        String id = readNonEmpty("Material ID: ");
        int amount = readInt("Quantity received: ", 1, Integer.MAX_VALUE);
        try {
            Material m = inventory.receiveStock(id, amount);
            if (m == null) {
                System.out.println("No material with ID '" + id + "' found.");
            } else {
                System.out.println("Updated: " + m.getName() + " now has " + m.getQuantity() + " " + m.getUnit());
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void issueStock() {
        System.out.println("--- Issue Stock to Production ---");
        String id = readNonEmpty("Material ID: ");
        int amount = readInt("Quantity to issue: ", 1, Integer.MAX_VALUE);
        try {
            Material m = inventory.issueToProduction(id, amount);
            if (m == null) {
                System.out.println("No material with ID '" + id + "' found.");
            } else {
                System.out.println("Issued. " + m.getName() + " now has " + m.getQuantity() + " " + m.getUnit());
                if (m.isLowStock()) {
                    System.out.println("WARNING: stock is at or below the reorder level (" + m.getReorderLevel() + ")!");
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void searchMenu() {
        System.out.println("--- Search (Linear Search) ---");
        System.out.println("1. By Material ID (exact)");
        System.out.println("2. By Name (contains)");
        System.out.println("3. By Category (exact)");
        System.out.println("4. By Supplier (contains)");
        int choice = readInt("Search by: ", 1, 4);
        String key = readNonEmpty("Search text: ");

        DoublyLinkedList<Material> results = new DoublyLinkedList<>();
        switch (choice) {
            case 1 -> {
                Material m = inventory.searchById(key);
                if (m != null) {
                    results.addLast(m);
                }
            }
            case 2 ->
                results = inventory.searchByName(key);
            case 3 ->
                results = inventory.searchByCategory(key);
            case 4 ->
                results = inventory.searchBySupplier(key);
        }
        InventoryManager.displayList(results);
        System.out.println("(elements examined: " + inventory.lastSearchComparisons() + " of " + inventory.size() + ")");
    }

    private static void sortMenu() {
        if (inventory.size() < 2) {
            System.out.println("Need at least 2 records to sort.");
            return;
        }
        System.out.println("--- Sort (Merge Sort) ---");
        InventoryManager.SortField[] fields = InventoryManager.SortField.values();
        for (int i = 0; i < fields.length; i++) {
            System.out.println((i + 1) + ". " + fields[i].getLabel());
        }
        int f = readInt("Sort by: ", 1, fields.length);
        int order = readInt("1) Ascending  2) Descending: ", 1, 2);

        long start = System.nanoTime();
        long comparisons = inventory.sort(fields[f - 1], order == 1);
        double micros = (System.nanoTime() - start) / 1000.0;

        System.out.printf("Sorted by %s (%s). Comparisons: %d, time: %.1f microseconds.%n",
                fields[f - 1].getLabel(), order == 1 ? "ascending" : "descending", comparisons, micros);
        inventory.display(true);
    }

    private static void lowStock() {
        System.out.println("--- Low-Stock (Reorder) Report ---");
        InventoryManager.displayList(inventory.lowStockReport());
    }

    private static void loadSample() {
        inventory.loadSampleData();
        System.out.println("Sample data loaded (duplicates ignored). Records now: " + inventory.size());
    }

    // ------------------------------------------------------------ input helpers
    private static String readLine(String prompt) {
        System.out.print(prompt);
        if (!in.hasNextLine()) {           // end of input (e.g. piped input finished)
            System.out.println("\nEnd of input. Exiting.");
            System.exit(0);
        }
        return in.nextLine().trim();
    }

    private static String readNonEmpty(String prompt) {
        while (true) {
            String s = readLine(prompt);
            if (!s.isEmpty()) {
                return s;
            }
            System.out.println("  Value cannot be empty.");
        }
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            String s = readLine(prompt);
            try {
                int v = Integer.parseInt(s);
                if (v >= min && v <= max) {
                    return v;
                }
                System.out.println("  Enter a number between " + min + " and " + (max == Integer.MAX_VALUE ? "max" : max) + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            String s = readLine(prompt);
            try {
                double v = Double.parseDouble(s);
                if (Double.isFinite(v) && v >= 0) {
                    return v;
                }
                System.out.println("  Enter a finite, nonnegative number.");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number.");
            }
        }
    }

    private static LocalDate readDate(String prompt) {
        while (true) {
            String s = readLine(prompt);
            if (s.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(s);
            } catch (DateTimeParseException e) {
                System.out.println("  Use the format yyyy-mm-dd.");
            }
        }
    }
}
