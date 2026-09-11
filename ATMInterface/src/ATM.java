import java.util.ArrayList;
import java.util.Scanner;

public class ATM {

    private Bank bank;
    private Account currentAccount;
    private ArrayList<Transaction> transactions;
    private Scanner scanner;

    public ATM(Bank bank) {
        this.bank = bank;
        this.transactions = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }

    public void start() {

        System.out.println("================================");
        System.out.println("       WELCOME TO ATM");
        System.out.println("================================");

        if (!login()) {
            System.out.println("\nAccess Denied. Exiting...");
            return;
        }

        showMenu();
    }

    private boolean login() {

        int attempts = 0;

        while (attempts < 3) {

            System.out.print("\nEnter User ID: ");
            String userId = scanner.nextLine();

            System.out.print("Enter PIN: ");
            int pin;

            try {
                pin = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid PIN format.");
                attempts++;
                continue;
            }

            Account account = bank.authenticate(userId, pin);

            if (account != null) {
                currentAccount = account;
                System.out.println("\nLogin Successful!");
                System.out.println("Welcome, " + currentAccount.getUserId());
                return true;
            }

            attempts++;
            System.out.println("Invalid User ID or PIN.");
            System.out.println("Attempts remaining: " + (3 - attempts));
        }

        return false;
    }

    private void showMenu() {

        while (true) {

            System.out.println("\n================================");
            System.out.println("          ATM MAIN MENU");
            System.out.println("================================");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Check Balance");
            System.out.println("6. Quit");
            System.out.println("================================");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    showTransactionHistory();
                    break;

                case "2":
                    withdraw();
                    break;

                case "3":
                    deposit();
                    break;

                case "4":
                    transfer();
                    break;

                case "5":
                    checkBalance();
                    break;

                case "6":
                    System.out.println("\nThank you for using our ATM.");
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void checkBalance() {

        System.out.println("\nCurrent Balance: ₹" +
                currentAccount.getBalance());
    }

    private void withdraw() {

        System.out.print("\nEnter withdrawal amount: ");
        double amount = readAmount();

        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        if (amount > currentAccount.getBalance()) {
            System.out.println("Insufficient Funds.");
            return;
        }

        if (currentAccount.withdraw(amount)) {

            transactions.add(new Transaction(
                    "Withdraw",
                    amount,
                    "Cash withdrawn"
            ));

            System.out.println("Withdrawal successful.");
            System.out.println("Remaining Balance: ₹" +
                    currentAccount.getBalance());
        }
    }

    private void deposit() {

        System.out.print("\nEnter deposit amount: ");
        double amount = readAmount();

        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        currentAccount.deposit(amount);

        transactions.add(new Transaction(
                "Deposit",
                amount,
                "Cash deposited"
        ));

        System.out.println("Deposit successful.");
        System.out.println("Current Balance: ₹" +
                currentAccount.getBalance());
    }

    private void transfer() {

        System.out.print("\nEnter recipient Account ID: ");
        String receiverId = scanner.nextLine();

        System.out.print("Enter transfer amount: ");
        double amount = readAmount();

        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        if (receiverId.equals(currentAccount.getAccountId())) {
            System.out.println("Cannot transfer to your own account.");
            return;
        }

        if (amount > currentAccount.getBalance()) {
            System.out.println("Insufficient Funds.");
            return;
        }

        boolean success = bank.transfer(
                currentAccount.getAccountId(),
                receiverId,
                amount
        );

        if (success) {

            transactions.add(new Transaction(
                    "Transfer",
                    amount,
                    "Transferred to " + receiverId
            ));

            System.out.println("Transfer successful.");
            System.out.println("Remaining Balance: ₹" +
                    currentAccount.getBalance());

        } else {

            System.out.println("Transfer failed.");
            System.out.println("Please check the recipient Account ID.");
        }
    }

    private void showTransactionHistory() {

        System.out.println("\n================================");
        System.out.println("       TRANSACTION HISTORY");
        System.out.println("================================");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    private double readAmount() {

        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return -1;
        }
    }
}