import com.gcash.model.User;
import java.util.List;
import java.util.Scanner;
import com.gcash.model.Transaction;

// Add this import if UserService is in a package, e.g. com.gcash.service
// import com.gcash.service.UserService;

// Temporary stub implementation if UserService does not exist
class UserService {
    public User login(String username, String password) { return null; }
    public void cashIn(User user, double amount) {}
    public User getUserByUsername(String username) { return null; }
    public void transfer(User from, User to, double amount) {}
}

// Transaction class definition
// (Moved to Transaction.java)

public class GcashApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();
        // Ensure TransactionService is defined or imported
        TransactionService transactionService = new TransactionService();

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
}