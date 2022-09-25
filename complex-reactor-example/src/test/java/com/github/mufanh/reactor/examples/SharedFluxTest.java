package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SharedFluxTest {

    @Test
    public void shared() {
        Flux<Integer> source = Flux.range(0, 20)
                .delayElements(Duration.ofMillis(100))
                .doOnSubscribe(s -> log.info("new subscription for the cold publisher"));

        Flux<Integer> cachedSource = source.share();
        cachedSource.subscribe(s -> log.info("[S 1] onNext: {}", s));
        try {
            TimeUnit.MICROSECONDS.sleep(2000);
        } catch (InterruptedException ignored) {
        }
        cachedSource.subscribe(s -> log.info("[S 2] onNext: {}", s));
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException ignored) {
        }
    }
}
