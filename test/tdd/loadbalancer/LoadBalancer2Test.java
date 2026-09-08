package tdd.loadbalancer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tdd.zpractice.loadbalancer.LoadBalancer2;
import tdd.zpractice.loadbalancer.Strategy;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LoadBalancer2Test {

    private LoadBalancer2 loadBalancer2;

    @BeforeEach
    void setUp() {
        loadBalancer2 = new LoadBalancer2();
    }

    @Test
    void registerAndGetSuccess() {
        loadBalancer2.register("10.0.0.1");
        assertThat(loadBalancer2.get()).isEqualTo("10.0.0.1");
    }

    @Test
    void shouldSaveSizeOfServers() {
        loadBalancer2.register("10.0.0.1");
        loadBalancer2.register("10.0.0.2");
        loadBalancer2.register("10.0.0.3");

        assertThat(loadBalancer2.size()).isEqualTo(3);
    }

    @Test
    void serversShouldBeRandomStrategy() {
        loadBalancer2.register("10.0.0.1");
        loadBalancer2.register("10.0.0.2");
        loadBalancer2.register("10.0.0.3");

        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 100; i++) {
             seen.add(loadBalancer2.get());
        }

        assertThat(seen).containsExactlyInAnyOrder("10.0.0.1", "10.0.0.2", "10.0.0.3");
    }

    @Test
    void get_shouldBeRoundRobinStrategy() {
        LoadBalancer2 balancerRR = new LoadBalancer2(Strategy.ROUND_ROBIN);

        balancerRR.register("10.0.0.1");
        balancerRR.register("10.0.0.2");
        balancerRR.register("10.0.0.3");

        assertThat(balancerRR.get()).isEqualTo("10.0.0.1");
        assertThat(balancerRR.get()).isEqualTo("10.0.0.2");
        assertThat(balancerRR.get()).isEqualTo("10.0.0.3");
        assertThat(balancerRR.get()).isEqualTo("10.0.0.1");
    }

    @Test
    void deregister_decreasesSize() {
        loadBalancer2.register("10.0.0.1");
        loadBalancer2.register("10.0.0.2");
        loadBalancer2.register("10.0.0.3");

        loadBalancer2.deregister("10.0.0.2");

        assertThat(loadBalancer2.size()).isEqualTo(2);

    }

    @Test
    void deregister_throwsWhenServerNotFound() {
        assertThatThrownBy(()-> loadBalancer2.deregister("10.0.0.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void get_throwsWhenNoServersRegistered() {
        assertThatThrownBy(()-> loadBalancer2.get())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void register_throwsWhenServerDuplicate() {
        loadBalancer2.register("10.0.0.1");

        assertThatThrownBy(()-> loadBalancer2.register("10.0.0.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }






}
