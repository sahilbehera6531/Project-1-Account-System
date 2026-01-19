
public class Account {

    private final String accountNumber;
    private String holderName;
    private double balance;

    public Account(String accNo, String name, double bal) {
        if(accNo == null || accNo.isEmpty())
            throw new IllegalArgumentException("Invalid Acc No");

        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("Invalid Name");

        if(bal < 0)
            throw new IllegalArgumentException("Negative Balance");

        this.accountNumber = accNo;
        this.holderName = name;
        this.balance = bal;
    }

    public void deposit(double amount) {
        if(amount <= 0)
            throw new IllegalArgumentException("Invalid deposit amount");

        balance += amount;
    }

    public void withdraw(double amount) {
        if(amount <= 0 || amount > balance)
            throw new IllegalArgumentException("Invalid withdrawal");

        balance -= amount;
    }

    public double getBalance() {
        return balance;
    }
}
