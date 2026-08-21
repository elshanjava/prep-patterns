package concurrent.zpractice.readwritelock;

import java.time.Instant;

public record FxRate(String pair, double rate, Instant updatedAt) {}
