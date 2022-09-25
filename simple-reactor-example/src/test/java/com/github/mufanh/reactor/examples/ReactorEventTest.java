package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;

@Slf4j
public class ReactorEventTest {

    @Test
    public void doOnEach() {
        Flux.just(1, 2, 3, 4)
                .concatWith(Flux.error(new RuntimeException("Conn error")))
                .concatWith(Flux.just(5, 6))
                .doOnEach(s -> log.info("Signal: {}", s))
                .subscribe();
    }

    @Test
    public void doOnNextAndOnError() {
        Flux.just(1, 2, 3, 4)
                .concatWith(Flux.error(new RuntimeException("Conn error")))
                .concatWith(Flux.just(5, 6))
                .doOnNext(s -> log.info("onNext: {}", s))
                .doOnError(e -> log.info("onError: {}", e.getMessage()))
                .subscribe();
    }
}
