package com.github.mufanh.reactor.examples;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;

@Slf4j
public class ReactorMaterializeTest {

    @Test
    public void materialize() {
        Flux.range(1, 3)
                .doOnNext(e -> log.info("data: {}", e))
                .materialize()
                .doOnNext(e -> log.info("signal: {}", e))
                .dematerialize()
                .collectList()
                .subscribe(r -> log.info("result: {}", r));
    }
}
