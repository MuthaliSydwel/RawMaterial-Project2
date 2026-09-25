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

/**
 * One raw material record used in production.
 */
public final class Material {

    private final String id;
    private final String name;
    private final String category;
    private final String unit;
    private final String supplier;
    private int quantity;
    private int reorderLevel;
    private double unitCost;
    private LocalDate dateReceived;

    public Material(String id, String name, String category, String unit, String supplier,
            int quantity, int reorderLevel, double unitCost, LocalDate dateReceived) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.supplier = supplier;
        setQuantity(quantity);
        if (reorderLevel < 0) {
            throw new IllegalArgumentException("Reorder level cannot be negative.");
        }
        if (!Double.isFinite(unitCost) || unitCost < 0) {
            throw new IllegalArgumentException("Unit cost must be finite and nonnegative.");
        }
        this.reorderLevel = reorderLevel;
        this.unitCost = unitCost;
        this.dateReceived = dateReceived;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getUnit() {
        return unit;
    }

    public String getSupplier() {
        return supplier;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public LocalDate getDateReceived() {
        return dateReceived;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    public void setDateReceived(LocalDate dateReceived) {
        this.dateReceived = dateReceived;
    }

    /**
     * Stock is low when it has fallen to (or below) the reorder level.
     */
    public boolean isLowStock() {
        return quantity <= reorderLevel;
    }

    public double getTotalValue() {
        return quantity * unitCost;
    }

    public static String tableHeader() {
        return String.format("%-8s %-20s %-12s %8s %-6s %9s %8s %-14s %-10s %s",
                "ID", "Name", "Category", "Qty", "Unit", "UnitCost", "Reorder", "Supplier", "Received", "Status")
                + "\n" + "-".repeat(114);
    }

    @Override
    public String toString() {
        return String.format("%-8.8s %-20.20s %-12.12s %8d %-6.6s %9.2f %8d %-14.14s %-10s %s",
                id, name, category, quantity, unit, unitCost, reorderLevel, supplier, dateReceived,
                isLowStock() ? "LOW STOCK" : "OK");
    }
}
