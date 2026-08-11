package concurrent.zpractice.circuitbreaker;

public class CircuitOpenStageException extends RuntimeException {
    public CircuitOpenStageException(String message) {
        super(message);
    }
}
