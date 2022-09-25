package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ReactorFlatMapTest {

    @Test
    public void flatMap() {
        Flux.just("user-1", "user-2", "user-3")
                .flatMap(u -> requestBooks(u)
                        .map(b -> u + "/" + b))
                .subscribe(e -> log.info("onNext: {}", e));
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ignored) {
        }
    }

    @Test
    public void flatMapSequential() {
        Flux.just("user-1", "user-2", "user-3")
                .flatMapSequential(u -> requestBooks(u)
                        .map(b -> u + "/" + b))
                .subscribe(e -> log.info("onNext: {}", e));

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ignored) {
        }
    }

    private Flux<String> requestBooks(String user) {
        return Flux.range(1, new Random().nextInt(3) + 1)
                .map(i -> "book-" + i)
                .delayElements(Duration.ofMillis(3));
    }
}
