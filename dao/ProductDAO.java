package com.inventory.dao;

import com.inventory.config.DBConnection;
import java.sql.*;

/*
 * Author : Myakala Swetha
 * Layer  : Data Access Object (DAO) for Oracle DB Operations
 */
public class ProductDAO {

    // 1. Display All Products
    public void displayProducts() {
        String sql = "SELECT * FROM products ORDER BY product_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n------------------------------------------------");
            System.out.printf("%-5s %-15s %-10s %-10s\n", "ID", "Product Name", "Price (INR)", "Stock");
            System.out.println("------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-5d %-15s %-10.2f %-10d\n",
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"));
            }
            System.out.println("------------------------------------------------");
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        }
    }

    // 2. Add New Product (Parameterized to prevent SQL Injection)
    public void addProduct(String name, double price, int stock) {
        String sql = "INSERT INTO products (product_name, price, stock) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, stock);
            pstmt.executeUpdate();
            System.out.println("✅ SUCCESS: New product added to inventory!");
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        }
    }

    // 3. Process Billing & Update Oracle DB Stock Dynamically
    public void processBilling(int productId, int quantity) {
        String checkSql = "SELECT product_name, price, stock FROM products WHERE product_id = ?";
        String updateSql = "UPDATE products SET stock = stock - ? WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, productId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int currentStock = rs.getInt("stock");
                double price = rs.getDouble("price");
                String name = rs.getString("product_name");

                if (currentStock >= quantity) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, quantity);
                        updateStmt.setInt(2, productId);
                        updateStmt.executeUpdate();

                        double total = price * quantity;
                        System.out.println("\n=========================================");
                        System.out.println("           TAX INVOICE / RECEIPT         ");
                        System.out.println("=========================================");
                        System.out.println("Item Purchased : " + name);
                        System.out.println("Quantity       : " + quantity);
                        System.out.println("Total Amount   : ₹" + total);
                        System.out.println("Payment Status : PAID");
                        System.out.println("Database Sync  : Stock updated dynamically");
                        System.out.println("=========================================");
                    }
                } else {
                    System.out.println("❌ ERROR: Insufficient stock! Available: " + currentStock);
                }
            } else {
                System.out.println("❌ ERROR: Product ID not found!");
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        }
    }
}