package tdd.pubsub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tdd.zpractice.pubsub.EventBus2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EventBus2Test {
    record PaymentCompleted(String paymentId, BigDecimal amount){}

    private EventBus2 eventBus;

    @BeforeEach
    void setUp() {
        eventBus = new EventBus2();
    }

    @Test
    void publish_receiveEvent() {
        List<PaymentCompleted> log = new ArrayList<>();

        var event = new PaymentCompleted("pay-1", new BigDecimal(100));
        eventBus.subscribe(PaymentCompleted.class, log::add);
        eventBus.publish(event);

        assertThat(log).hasSize(1);
        assertThat(log).containsExactly(event);
    }
}
