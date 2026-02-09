package util;

public class TransactionIdGenerator {

    public static String generate() {
        return "TXN-" + System.nanoTime();
    }
}
