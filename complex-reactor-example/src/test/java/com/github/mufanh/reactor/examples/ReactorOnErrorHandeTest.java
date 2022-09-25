package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Random;

@Slf4j
public class ReactorOnErrorHandeTest {

    private static final Random random = new Random();

    @Test
    public void retryBackoff() {
        Flux.just("user-1")
                .flatMap(user -> recommendedBooks(user)
                        //.retry(5)
                        .retryWhen(Retry.backoff(5, Duration.ofMillis(100)))
                        .timeout(Duration.ofSeconds(3))
                        .onErrorResume(e -> Flux.just("The Martian")))
                .subscribe(
                        b -> log.info("onNext: {}", b),
                        e -> log.warn("onError: {}", e.getMessage()),
                        () -> log.info("onComplete")
                );

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
    }

    private Flux<String> recommendedBooks(String userId) {
        return Flux.defer(() -> {
            if (random.nextInt(10) < 7) {
                return Flux.error(new RuntimeException("Err:" + 7));
            }
            return Flux.just("Blue Mars", "The Expanse")
                    .delayElements(Duration.ofMillis(100));
        }).doOnSubscribe(s -> log.info("Request for {}", userId));
    }
}
