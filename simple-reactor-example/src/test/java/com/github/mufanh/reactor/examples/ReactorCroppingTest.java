package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.Arrays;

@Slf4j
public class ReactorCroppingTest {

    @Test
    public void any() {
        Flux.just(3, 5, 7, 9, 11, 15, 16, 17)
                .any(e -> e % 2 == 0)
                .subscribe(hasEvens -> log.info("Has evens: {}", hasEvens));
    }

    @Test
    public void reduce() {
        Flux.range(1, 5)
                .reduce(0, Integer::sum)
                .subscribe(result -> log.info("result: {}", result));
    }

    @Test
    public void scan() {
        Flux.range(1, 5)
                .scan(0, Integer::sum)
                .subscribe(result -> log.info("result: {}", result));

        int bucketSize = 5;
        Flux.range(1, 500)
                .index()
                .scan(
                        new int[bucketSize],
                        (acc, elem) -> {
                            acc[(int)(elem.getT1() % bucketSize)] = elem.getT2();
                            return acc;
                        })
                .skip(bucketSize)
                .map(array -> Arrays.stream(array).sum() * 1.0 / bucketSize)
                .subscribe(av -> log.info("running average: {}", av));
    }

    @Test
    public void testThen() {
        Flux.just(1, 2, 3)
                .thenMany(Flux.just(4, 5))
                .subscribe(e -> log.info("onNext: {}", e));
    }
}
