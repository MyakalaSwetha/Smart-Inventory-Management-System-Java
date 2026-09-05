package com.inventory.main;

import com.inventory.dao.ProductDAO;
import java.util.Scanner;

/*
 * Author      : Myakala Swetha
 * Application : Smart Inventory & Billing Management System
 */
public class MainApp {
    public static void main(String[] args) {
        ProductDAO dao = new ProductDAO();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n================================================");
            System.out.println("   MYAKALA SWETHA'S INVENTORY & BILLING ENGINE   ");
            System.out.println("================================================");
            System.out.println("1. View Current Inventory Stock");
            System.out.println("2. Add New Product to Database");
            System.out.println("3. Process Billing & Sell Item");
            System.out.println("4. Exit System");
            System.out.print("Enter your choice (1-4): ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    dao.displayProducts();
                    break;
                case 2:
                    System.out.print("Enter Product Name: ");
                    String name = scanner.next();
                    System.out.print("Enter Price per unit: ");
                    double price = scanner.nextDouble();
                    System.out.print("Enter Stock Quantity: ");
                    int stock = scanner.nextInt();
                    dao.addProduct(name, price, stock);
                    break;
                case 3:
                    System.out.print("Enter Product ID to Buy: ");
                    int id = scanner.nextInt();
                    System.out.print("Enter Quantity to Purchase: ");
                    int qty = scanner.nextInt();
                    dao.processBilling(id, qty);
                    break;
                case 4:
                    System.out.println("\nExiting System... Thank you for using Swetha's Inventory System!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("❌ Invalid Choice! Please enter a number between 1 and 4.");
            }
        }
    }
}