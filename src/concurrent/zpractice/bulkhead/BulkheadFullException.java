package concurrent.zpractice.bulkhead;

final class BulkheadFullException extends RuntimeException {
    BulkheadFullException(String msg) { super(msg); }
}
