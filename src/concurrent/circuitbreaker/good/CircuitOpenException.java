package concurrent.circuitbreaker.good;

final class CircuitOpenException extends RuntimeException {
    CircuitOpenException(String msg) { super(msg); }
}
