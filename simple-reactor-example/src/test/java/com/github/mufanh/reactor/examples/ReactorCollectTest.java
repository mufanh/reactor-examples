package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.Comparator;

@Slf4j
public class ReactorCollectTest {

    @Test
    public void collectSortedList() {
        Flux.just(1, 6, 2, 8, 3, 1, 5, 1)
                .collectSortedList(Comparator.reverseOrder())
                .subscribe(e -> log.info("{}", e));
    }

    @Test
    public void distinctUntilChanged() {
        Flux.just(1, 1, 1, 2, 2, 2, 3, 2, 1, 1, 4)
                .distinctUntilChanged()
                .subscribe(e -> log.info("{}", e));
    }
}
