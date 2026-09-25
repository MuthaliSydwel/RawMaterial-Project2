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
import java.util.Comparator;

/**
 * Business logic for the Raw Material Inventory. All records live in our own
 * DoublyLinkedList; searching uses our LinearSearch; sorting uses our
 * MergeSort.
 */
public class InventoryManager {

    /**
     * Fields the inventory can be sorted on.
     */
    public enum SortField {
        ID("Material ID", Comparator.comparing(Material::getId, String.CASE_INSENSITIVE_ORDER)),
        NAME("Name", Comparator.comparing(Material::getName, String.CASE_INSENSITIVE_ORDER)),
        CATEGORY("Category", Comparator.comparing(Material::getCategory, String.CASE_INSENSITIVE_ORDER)),
        QUANTITY("Quantity in stock", Comparator.comparingInt(Material::getQuantity)),
        UNIT_COST("Unit cost", Comparator.comparingDouble(Material::getUnitCost)),
        DATE_RECEIVED("Date received", Comparator.comparing(Material::getDateReceived));

        final String label;
        final Comparator<Material> comparator;

        SortField(String label, Comparator<Material> comparator) {
            this.label = label;
            this.comparator = comparator;
        }

        public String getLabel() {
            return label;
        }
    }

    private final DoublyLinkedList<Material> materials = new DoublyLinkedList<>();

    // ---------------------------------------------------------------- insertion
    /**
     * O(n) duplicate-ID check, then O(1) insertion at the end. Returns false
     * for a duplicate.
     */
    public boolean addMaterial(Material m) {
        if (findNodeById(m.getId()) != null) {
            return false;
        }
        materials.addLast(m);
        return true;
    }

    /**
     * Bulk loading of trusted, pre-validated data: O(1), skips the duplicate-ID
     * scan.
     */
    void addMaterialFast(Material m) {
        materials.addLast(m);
    }

    /**
     * O(n) duplicate-ID check, then O(1) insertion at the front. Returns false
     * for a duplicate.
     */
    public boolean addMaterialAtFront(Material m) {
        if (findNodeById(m.getId()) != null) {
            return false;
        }
        materials.addFirst(m);
        return true;
    }

    // ------------------------------------------------------------------ removal
    /**
     * Linear search O(n) to find the node, then O(1) unlink. Returns removed
     * record or null.
     */
    public Material removeMaterial(String id) {
        DoublyLinkedList.Node<Material> node = findNodeById(id);
        if (node == null) {
            return null;
        }
        return materials.removeNode(node);
    }

    // ------------------------------------------------------ stock movements
    /**
     * Receives positive stock without overflow. Returns updated record or null
     * if ID not found.
     */
    public Material receiveStock(String id, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Quantity received must be positive.");
        }
        DoublyLinkedList.Node<Material> node = findNodeById(id);
        if (node == null) {
            return null;
        }
        if (amount > Integer.MAX_VALUE - node.data.getQuantity()) {
            throw new IllegalArgumentException("Cannot receive stock: total quantity would exceed "
                    + Integer.MAX_VALUE + ".");
        }
        node.data.setQuantity(node.data.getQuantity() + amount);
        node.data.setDateReceived(LocalDate.now());
        return node.data;
    }

    /**
     * Issues material to production. Returns: updated record on success, null
     * if ID not found. Throws IllegalArgumentException if the amount is
     * nonpositive or exceeds available stock.
     */
    public Material issueToProduction(String id, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Quantity to issue must be positive.");
        }
        DoublyLinkedList.Node<Material> node = findNodeById(id);
        if (node == null) {
            return null;
        }
        if (amount > node.data.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock: only "
                    + node.data.getQuantity() + " " + node.data.getUnit() + " available.");
        }
        node.data.setQuantity(node.data.getQuantity() - amount);
        return node.data;
    }

    // ---------------------------------------------------------------- searching
    private DoublyLinkedList.Node<Material> findNodeById(String id) {
        return LinearSearch.findFirst(materials, m -> m.getId().equalsIgnoreCase(id));
    }

    public Material searchById(String id) {
        DoublyLinkedList.Node<Material> node = findNodeById(id);
        return node == null ? null : node.data;
    }

    public DoublyLinkedList<Material> searchByName(String text) {
        String t = text.toLowerCase();
        return LinearSearch.findAll(materials, m -> m.getName().toLowerCase().contains(t));
    }

    public DoublyLinkedList<Material> searchByCategory(String category) {
        return LinearSearch.findAll(materials, m -> m.getCategory().equalsIgnoreCase(category));
    }

    public DoublyLinkedList<Material> searchBySupplier(String text) {
        String t = text.toLowerCase();
        return LinearSearch.findAll(materials, m -> m.getSupplier().toLowerCase().contains(t));
    }

    public DoublyLinkedList<Material> lowStockReport() {
        return LinearSearch.findAll(materials, Material::isLowStock);
    }

    public long lastSearchComparisons() {
        return LinearSearch.getLastComparisons();
    }

    // ------------------------------------------------------------------ sorting
    /**
     * Sorts with merge sort. Returns the number of comparisons made.
     */
    public long sort(SortField field, boolean ascending) {
        Comparator<Material> cmp = ascending ? field.comparator : field.comparator.reversed();
        return MergeSort.sort(materials, cmp);
    }

    // ------------------------------------------------------------------ display
    public void display(boolean forward) {
        if (materials.isEmpty()) {
            System.out.println("  (inventory is empty)");
            return;
        }
        System.out.println(Material.tableHeader());
        if (forward) {
            materials.forEachForward(System.out::println); 
        }else {
            materials.forEachBackward(System.out::println);
        }
        System.out.printf("%d record(s). Total stock value: %,.2f%n", materials.size(), totalValue());
    }

    public static void displayList(DoublyLinkedList<Material> list) {
        if (list.isEmpty()) {
            System.out.println("  (no matching records)");
            return;
        }
        System.out.println(Material.tableHeader());
        list.forEachForward(System.out::println);
        System.out.println(list.size() + " record(s) found.");
    }

    public double totalValue() {
        double total = 0;
        for (Material m : materials) {
            total += m.getTotalValue();
        }
        return total;
    }

    public int size() {
        return materials.size();
    }

    public boolean isEmpty() {
        return materials.isEmpty();
    }

    public void clear() {
        materials.clear();
    }

    // -------------------------------------------------------------- sample data
    public void loadSampleData() {
        LocalDate d = LocalDate.of(2026, 9, 1);
        addMaterial(new Material("RM007", "Steel Sheet 2mm", "Metal", "sheet", "SteelCo", 120, 50, 350.00, d.plusDays(3)));
        addMaterial(new Material("RM002", "Copper Wire", "Metal", "kg", "WireWorks", 40, 60, 145.50, d.plusDays(10)));
        addMaterial(new Material("RM015", "Portland Cement", "Aggregate", "bag", "BuildMart", 300, 100, 95.00, d));
        addMaterial(new Material("RM011", "River Sand", "Aggregate", "ton", "SandPro", 25, 30, 210.00, d.plusDays(6)));
        addMaterial(new Material("RM004", "Industrial Paint Red", "Chemical", "litre", "ColorTech", 15, 20, 88.75, d.plusDays(14)));
        addMaterial(new Material("RM009", "Polyethylene Pellets", "Plastic", "kg", "PlastiCorp", 800, 200, 32.40, d.plusDays(1)));
        addMaterial(new Material("RM001", "Aluminium Rod", "Metal", "m", "SteelCo", 75, 40, 120.00, d.plusDays(8)));
        addMaterial(new Material("RM013", "Solvent Thinner", "Chemical", "litre", "ColorTech", 60, 25, 45.00, d.plusDays(12)));
        addMaterial(new Material("RM006", "Rubber Gasket", "Rubber", "pcs", "SealIt", 500, 150, 6.25, d.plusDays(4)));
        addMaterial(new Material("RM010", "Oak Timber Plank", "Wood", "pcs", "TimberLand", 18, 25, 275.00, d.plusDays(2)));
    }
}
