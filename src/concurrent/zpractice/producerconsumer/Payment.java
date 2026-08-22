package concurrent.zpractice.producerconsumer;

public record Payment(String id, long amountCents) {
    static final Payment POISON = new Payment("__POISON__", 0);
}
