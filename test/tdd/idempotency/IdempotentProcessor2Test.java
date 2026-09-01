package tdd.idempotency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tdd.zpractice.IdempotentProcessor2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class IdempotentProcessor2Test {

   private IdempotentProcessor2 idempotentProcessor;

   @BeforeEach
   void setUp() {
       idempotentProcessor = new IdempotentProcessor2();
   }

//  1. первый вызов выполняет action и возвращает результат

    @Test
    void action_shouldReturnResult() throws Exception {
       String result = idempotentProcessor.process("req-id", ()-> "action");

       assertThat(result).isEqualTo("action");

    }

//  2. тот же requestId → тот же результат, action НЕ вызывается повторно

    @Test
    void process_sameRequestsOnce() throws Exception {
       var counter = new AtomicInteger(0);

       idempotentProcessor.process("req-id", ()-> {counter.incrementAndGet(); return "action";});
       idempotentProcessor.process("req-id", ()-> {counter.incrementAndGet(); return "action";});
       idempotentProcessor.process("req-id", ()-> {counter.incrementAndGet(); return "action";});

       assertThat(counter.get()).isEqualTo(1);

    }

//  3. разные requestId → выполняются оба
    @Test
    void process_differentRequests() throws Exception {
        var counter = new AtomicInteger(0);

        idempotentProcessor.process("req-id-1", ()-> {counter.incrementAndGet(); return "action";});
        idempotentProcessor.process("req-id-2", ()-> {counter.incrementAndGet(); return "action";});

        assertThat(counter.get()).isEqualTo(2);
    }

//  4. action упал → результат не кэшируется, повтор возможен
//  5. 50 потоков с одним requestId → ровно одно выполнение


}
