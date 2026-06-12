package structural.facade;

// То, что фасад отдаёт наружу. Клиент видит этот простой тип, а не кишки PSP.
public record PaymentResult(String authId, long amountCents) {
    public static PaymentResult of(Authorization auth) {
        return new PaymentResult(auth.id(), auth.amountCents());
    }

    @Override public String toString() {
        return "PaymentResult{auth=" + authId + ", amount=" + amountCents + " cents}";
    }
}
