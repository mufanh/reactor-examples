package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ReactorElapsedTest {

    @Test
    public void elapsed() {
        Flux.range(0, 5)
                .delayElements(Duration.ofMillis(100))
                .elapsed()
                .subscribe(e -> log.info("Elapsed {} ms: {}", e.getT1(), e.getT2()));

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ignored) {
        }
    }
}
