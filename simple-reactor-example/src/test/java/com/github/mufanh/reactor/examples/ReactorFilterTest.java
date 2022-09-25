package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
public class ReactorFilterTest {

    @Test
    public void skipUntilOther() {
        Flux.fromStream(Stream.iterate(0, e -> e + 1))
                .skipUntilOther(Mono.delay(Duration.ofSeconds(1)))
                .takeUntilOther(Mono.delay(Duration.ofSeconds(10)))
                .subscribe(e -> {
                    log.info("{}", e);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ignore) {
                    }
                });
    }
}
