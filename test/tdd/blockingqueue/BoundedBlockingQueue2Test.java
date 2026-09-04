package tdd.blockingqueue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tdd.zpractice.blockingqueue.BoundedBlockingQueue2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class BoundedBlockingQueue2Test {

  private BoundedBlockingQueue2<String> blockingQueue2;

  @BeforeEach
  void setUp() {
    blockingQueue2 = new BoundedBlockingQueue2<>(5);
  }

  @Test
  void putThenTakeElement() {
    blockingQueue2.put("a");
    assertThat(blockingQueue2.take()).isEqualTo("a");
  }

  @Test
  void putAndReturnInFifoOrder() {
    blockingQueue2.put("a");
    blockingQueue2.put("b");

    assertThat(blockingQueue2.take()).isEqualTo("a");
    assertThat(blockingQueue2.take()).isEqualTo("b");
  }
}
