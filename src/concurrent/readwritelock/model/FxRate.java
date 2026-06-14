package concurrent.readwritelock.model;

import java.time.Instant;

public record FxRate(String pair, double rate, Instant updatedAt) {}
