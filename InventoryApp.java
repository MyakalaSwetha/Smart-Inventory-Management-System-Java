package com.inventory.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class InventoryApp {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521:Free";
    private static final String USER = "system";
    private static final String PASSWORD = "Gnan123"; 

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("✅ Oracle Database Connected Successfully!\n");

            while (true) {
                System.out.println("=== SMART INVENTORY MANAGEMENT SYSTEM (ORACLE) ===");
                System.out.println("1. View All Products");
                System.out.println("2. Add New Product");
                System.out.println("3. Process Billing / Sell Product");
                System.out.println("4. Exit");
                System.out.print("Choose Option: ");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        viewProducts(conn);
                        break;
                    case 2:
                        addProduct(conn, scanner);
                        break;
                    case 3:
                        processBilling(conn, scanner);
                        break;
                    case 4:
                        System.out.println("Exiting System... Thank you!");
                        return;
                    default:
                        System.out.println("Invalid Option! Try again.\n");
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Database Connection Failed: " + e.getMessage());
        }
    }

    private static void viewProducts(Connection conn) throws SQLException {
        String sql = "SELECT * FROM products ORDER BY product_id";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n------------------------------------------------");
            System.out.printf("%-5s %-20s %-10s %-10s\n", "ID", "Name", "Price", "Stock");
            System.out.println("------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-5d %-20s %-10.2f %-10d\n",
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"));
            }
            System.out.println("------------------------------------------------\n");
        }
    }

    private static void addProduct(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Product Name: ");
        scanner.nextLine();
        String name = scanner.nextLine();
        System.out.print("Enter Price: ");
        double price = scanner.nextDouble();
        System.out.print("Enter Stock Quantity: ");
        int qty = scanner.nextInt();

        String sql = "INSERT INTO products (product_name, price, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, qty);
            pstmt.executeUpdate();
            System.out.println("✅ Product Added Successfully!\n");
        }
    }

    private static void processBilling(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Product ID to Buy: ");
        int id = scanner.nextInt();
        System.out.print("Enter Quantity: ");
        int buyQty = scanner.nextInt();

        String selectSql = "SELECT price, quantity FROM products WHERE product_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double price = rs.getDouble("price");
                int currentStock = rs.getInt("quantity");

                if (currentStock >= buyQty) {
                    double totalBill = price * buyQty;
                    String updateSql = "UPDATE products SET quantity = quantity - ? WHERE product_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, buyQty);
                        updateStmt.setInt(2, id);
                        updateStmt.executeUpdate();
                    }
                    System.out.println("\n================ INVOICE ================");
                    System.out.println("Total Amount: ₹" + totalBill);
                    System.out.println("Status: PAID & Stock Updated in Oracle Database!");
                    System.out.println("=========================================\n");
                } else {
                    System.out.println("❌ Insufficient Stock! Available: " + currentStock + "\n");
                }
            } else {
                System.out.println("❌ Product Not Found!\n");
            }
        }
    }
}