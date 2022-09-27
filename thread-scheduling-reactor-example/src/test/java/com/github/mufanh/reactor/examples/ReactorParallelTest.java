package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class ReactorParallelTest {

    @Test
    public void parallel() {
        Flux.range(0, 10000)
                // 顺序就不可控了
                .parallel()
                .runOn(Schedulers.parallel())
                .subscribe(e -> log.info("onNext: {}", e));
    }
}
