import java.util.HashMap;
import java.util.Map;

public class Bank {

    private Map<String, Account> accounts;

    public Bank() {
        accounts = new HashMap<>();

        // Sample accounts
        accounts.put("ACC001",
                new Account("ACC001", "Aashish", 1234, 10000));

        accounts.put("ACC002",
                new Account("ACC002", "Rahul", 5678, 8000));
    }

    public Account findAccount(String accountId) {
        return accounts.get(accountId);
    }

    public Account authenticate(String userId, int pin) {

        for (Account account : accounts.values()) {

            if (account.getUserId().equalsIgnoreCase(userId)
                    && account.getPin() == pin) {

                return account;
            }
        }

        return null;
    }

    public boolean transfer(String senderId, String receiverId, double amount) {

        Account sender = findAccount(senderId);
        Account receiver = findAccount(receiverId);

        if (sender == null || receiver == null) {
            return false;
        }

        if (amount <= 0 || amount > sender.getBalance()) {
            return false;
        }

        sender.withdraw(amount);
        receiver.deposit(amount);

        return true;
    }
}