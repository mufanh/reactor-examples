package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ReactorSpecialFilterTest {

    @Test
    public void skipAndTakeUtil() {
        Flux.range(1, 10000)
                .delayElements(Duration.ofMillis(100))
                .index()
                .skipUntilOther(Mono.delay(Duration.ofSeconds(1), Schedulers.parallel()))
                .takeUntilOther(Mono.delay(Duration.ofSeconds(10), Schedulers.parallel()))
                .subscribe(e -> log.info("index: {}, data: {}", e.getT1(), e.getT2()));

        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException ignored) {
        }
    }
}
