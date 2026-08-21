package concurrent.bulkhead.good;

final class BulkheadFullException extends RuntimeException {
    BulkheadFullException(String msg) { super(msg); }
}
