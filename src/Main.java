public class Main {
    public static void main(String[] args) {

        Account acc = new Account("123", "Sahil", 1000);

        acc.deposit(500);
        acc.withdraw(300);

        System.out.println(acc.getBalance());
    }
}
