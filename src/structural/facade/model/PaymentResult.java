package structural.facade.model;

public record PaymentResult(String authId, long amountCents) {
    public static PaymentResult of(Authorization auth) {
        return new PaymentResult(auth.id(), auth.amountCents());
    }

    @Override public String toString() {
        return "PaymentResult{auth=" + authId + ", amount=" + amountCents + " cents}";
    }
}
