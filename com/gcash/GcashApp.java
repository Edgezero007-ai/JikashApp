package com.gcash;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Date;

public class GcashApp {
    public static void main(String[] args) {
        UserService userService = new UserService();
        TransactionService transactionService = new TransactionService();
        Scanner scanner = new Scanner(System.in);

        // Login
        User currentUser = null;
        while (currentUser == null) {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            try {
                currentUser = userService.login(username, password);
                if (currentUser == null) {
                    System.out.println("Invalid credentials!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        System.out.println("Login successful! Welcome " + currentUser.getUsername());

        // Main menu
        boolean sessionActive = true;
        while (sessionActive) {
            System.out.println("\n[1] Check Balance");
            System.out.println("[2] Cash-In");
            System.out.println("[3] Transfer");
            System.out.println("[4] View Transactions");
            System.out.println("[5] Logout");
            System.out.print("Select option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1: // Check Balance
                    System.out.println("Balance: PHP " + currentUser.getBalance());
                    break;

                case 2: // Cash-In
                    System.out.print("Enter amount: ");
                    double amount = scanner.nextDouble();
                    try {
                        userService.cashIn(currentUser, amount);
                        System.out.println("Success! New balance: PHP " + currentUser.getBalance());
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 3: // Transfer
                    System.out.print("Recipient username: ");
                    scanner.nextLine(); // Consume newline
                    String recipient = scanner.nextLine();
                    System.out.print("Amount: ");
                    double transferAmount = scanner.nextDouble();

                    try {
                        User recipientUser = userService.getUserByUsername(recipient);
                        if (recipientUser == null) {
                            System.out.println("User not found!");
                        } else if (currentUser.getBalance() < transferAmount) {
                            System.out.println("Insufficient balance!");
                        } else {
                            userService.transfer(currentUser, recipientUser, transferAmount);
                            System.out.println("Transfer successful!");
                        }
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 4: // View Transactions
                    try {
                        List<Transaction> transactions = transactionService.getTransactions(currentUser.getUserId());
                        System.out.println("\n--- Transaction History ---");
                        for (Transaction t : transactions) {
                            String desc = switch (t.getType()) {
                                case "CASH_IN" -> "Cash In: +PHP " + t.getAmount();
                                case "TRANSFER_OUT" -> "To " + t.getRelatedUsername() + ": -PHP " + t.getAmount();
                                case "TRANSFER_IN" -> "From " + t.getRelatedUsername() + ": +PHP " + t.getAmount();
                                default -> "Unknown transaction";
                            };
                            System.out.println(desc + " | " + t.getTimestamp());
                        }
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 5: // Logout
                    sessionActive = false;
                    System.out.println("Logging out...");
                    break;

                default:
                    System.out.println("Invalid option!");
            }
        }
        scanner.close();
    }

    // Additional methods can be implemented here if needed.
}

class User {
    private String username;
    private int userId;
    private double balance;

    public User(String username, int userId, double balance) {
        this.username = username;
        this.userId = userId;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public int getUserId() {
        return userId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}

class UserService {
    public User login(String username, String password) {
        // Stub: always return a User for demonstration
        return new User(username, 1, 1000.0);
    }

    public void cashIn(User user, double amount) {
        user.setBalance(user.getBalance() + amount);
    }

    public User getUserByUsername(String username) {
        // Stub: always return a User for demonstration
        return new User(username, 2, 500.0);
    }

    public void transfer(User from, User to, double amount) {
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
    }
}

// Stub implementation for TransactionService and Transaction

class TransactionService {
    public List<Transaction> getTransactions(int userId) {
        // Stub: return some dummy transactions
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("CASH_IN", 1000.0, null, new Date()));
        transactions.add(new Transaction("TRANSFER_OUT", 200.0, "alice", new Date()));
        transactions.add(new Transaction("TRANSFER_IN", 300.0, "bob", new Date()));
        return transactions;
    }
}

class Transaction {
    private String type;
    private double amount;
    private String relatedUsername;
    private Date timestamp;

    public Transaction(String type, double amount, String relatedUsername, Date timestamp) {
        this.type = type;
        this.amount = amount;
        this.relatedUsername = relatedUsername;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getRelatedUsername() {
        return relatedUsername;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}