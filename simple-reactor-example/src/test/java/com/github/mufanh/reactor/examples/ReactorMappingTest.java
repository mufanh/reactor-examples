package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Instant;

@Slf4j
public class ReactorMappingTest {

    @Test
    public void map() {
        Flux.range(1, 5)
                .map(e -> 2 * e)
                .subscribe(e -> log.info("{}", e));
    }

    @Test
    public void indexAndTimestamp() {
        Flux.range(2018, 5)
                .timestamp()
                .index()
                .subscribe(e -> log.info("index: {}, ts: {}, value: {}",
                        e.getT1(), Instant.ofEpochMilli(e.getT2().getT1()), e.getT2().getT2()));

        log.info("------------------------------------------");

        Flux.range(2018, 5)
                .index()
                .timestamp()
                .subscribe(e -> log.info("ts: {}, index: {}, value: {}",
                        Instant.ofEpochMilli(e.getT1()), e.getT2().getT1(), e.getT2().getT2()));
    }
}
