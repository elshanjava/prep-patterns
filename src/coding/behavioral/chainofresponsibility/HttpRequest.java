package coding.behavioral.chainofresponsibility;

public record HttpRequest(String authToken, String clientId, long amountCents) {
}
