package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ReactorSampleTest {

    @Test
    public void sample() {
        Flux.range(1, 100)
                .delayElements(Duration.ofMillis(10))
                .sample(Duration.ofMillis(200))
                .subscribe(e -> log.info("onNext: {}", e));

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ignored) {
        }
    }
}
