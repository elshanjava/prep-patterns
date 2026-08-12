package concurrent.zpractice.completablefuture;

public record PspResponse(String psp, long feeMillis, boolean success) {}
