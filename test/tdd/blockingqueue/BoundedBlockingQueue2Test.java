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
    blockingQueue2 = new BoundedBlockingQueue2<>(3);
  }

  @Test
  void putThenTakeElement() throws InterruptedException {
    blockingQueue2.put("a");

    assertThat(blockingQueue2.take()).isEqualTo("a");
    assertThat(blockingQueue2.size()).isZero();
  }

  @Test
  void throwsWhenCapacityIsNotPositive() {

    assertThatThrownBy(()-> {
      new BoundedBlockingQueue2<>(-1);
    }).isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(()-> {
      new BoundedBlockingQueue2<>(0);
    }).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void takeReturnsElementsInFifoOrder() throws InterruptedException {
    blockingQueue2.put("a");
    blockingQueue2.put("b");
    blockingQueue2.put("c");

    assertThat(blockingQueue2.take()).isEqualTo("a");
    assertThat(blockingQueue2.take()).isEqualTo("b");
    assertThat(blockingQueue2.take()).isEqualTo("c");
    assertThat(blockingQueue2.size()).isZero();
  }

  @Test
  void wrapsAroundRingBuffer() throws InterruptedException {
    blockingQueue2.put("a");
    blockingQueue2.put("b");
    blockingQueue2.put("c");

    assertThat(blockingQueue2.take()).isEqualTo("a");
    assertThat(blockingQueue2.take()).isEqualTo("b");

    blockingQueue2.put("d");
    blockingQueue2.put("e");

    assertThat(blockingQueue2.take()).isEqualTo("c");
    assertThat(blockingQueue2.take()).isEqualTo("d");
    assertThat(blockingQueue2.take()).isEqualTo("e");
  }

  @Test
  void takeBlocksUntilElementAvailable() throws InterruptedException{
    var result = new AtomicReference<String>();
    var started = new CountDownLatch(1);

    Thread consumer = new Thread(()-> {
      started.countDown();
      try {
        result.set(blockingQueue2.take());
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

    });
    consumer.start();

    started.await();;
    Thread.sleep(1000);

    assertThat(result.get()).isNull();
    assertThat(consumer.isAlive()).isTrue();

    blockingQueue2.put("last");
    consumer.join();

    assertThat(consumer.isAlive()).isFalse();
    assertThat(result.get()).isEqualTo("last");

  }
}
