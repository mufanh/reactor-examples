package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.util.function.Function;

@Slf4j
public class ReactorTransformTest {

    @Test
    public void transform() {
        Function<Flux<String>, Flux<String>> logUserInfo =
                stream -> stream
                        .index()
                        .doOnNext(tp -> log.info("[{}] User: {}", tp.getT1(), tp.getT2()))
                        .map(Tuple2::getT2);

        Flux.range(1000, 3)
                .map(i -> "user-" + i)
                .transform(logUserInfo)
                .subscribe(e -> log.info("onNext: {}", e));

    }

    @Test
    public void notTransform() {
        Flux.range(1000, 3)
                .map(i -> "user-" + i)
                .index()
                .doOnNext(tp -> log.info("[{}] User: {}", tp.getT1(), tp.getT2()))
                .map(Tuple2::getT2)
                .subscribe(e -> log.info("onNext: {}", e));
    }
}
