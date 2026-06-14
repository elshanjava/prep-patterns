package concurrent.completablefuture.model;

public record PspResponse(String psp, long feeMillis, boolean success) {}
