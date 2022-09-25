package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

@Slf4j
public class ReactorFactoryTest {

    @Test
    public void push() {
        Flux.create(emitter -> IntStream.range(2000, 3000)
                .forEach(emitter::next))
                .delayElements(Duration.ofMillis(1))
                .subscribe(e -> log.info("onNext: {}", e));

        try {
            Thread.sleep(20000);
        } catch (InterruptedException ignored) {
        }
    }

    @Test
    public void generate() {
        Flux.generate(
                () -> Tuples.of(0L, 1L),
                (state, slink) -> {
                    log.info("generated value: {}", state.getT2());
                    slink.next(state.getT2());
                    long newValue = state.getT1() + state.getT2();
                    return Tuples.of(state.getT2(), newValue);
                })
                .delayElements(Duration.ofMillis(1))
                .take(7)
                .subscribe(e -> log.info("onNext: {}", e));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
    }
}
