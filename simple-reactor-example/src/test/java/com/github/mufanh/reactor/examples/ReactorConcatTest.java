package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;

@Slf4j
public class ReactorConcatTest {

    @Test
    public void concat() {
        Flux.concat(Flux.range(1, 3), Flux.range(4, 2), Flux.range(6, 5))
                .subscribe(e -> log.info("{}", e));
    }

    @Test
    public void merge() {
        // 都是立即订阅的，并非一个一个处理，此处用简单的range测试不出来
        Flux.merge(Flux.range(1, 3), Flux.range(4, 2), Flux.range(6, 5))
                .subscribe(e -> log.info("{}", e));
    }

    @Test
    public void concatException() {
        Flux.concat(Flux.range(1, 3), Flux.error(new RuntimeException("appError")), Flux.range(4, 2))
                .subscribe(e -> log.info("{}", e));
    }

    @Test
    public void concatExceptionReturn() {
        Flux.concat(Flux.range(1, 3), Flux.error(new RuntimeException("appError")), Flux.range(4, 2))
                .onErrorReturn(9999)
                .subscribe(e -> log.info("{}", e));
    }
}
