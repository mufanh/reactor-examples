package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.LinkedList;

@Slf4j
public class ReactorBufferTest {

    @Test
    public void buffer() {
        Flux.range(1, 13)
                .buffer(4)
                .subscribe(e -> log.info("{}", e));
    }

    @Test
    public void window() {
        Flux<Flux<Integer>> windowedFlux = Flux.range(101, 20)
                .windowUntil(this::isPrime, true);
        windowedFlux.subscribe(window ->
                window.collectList()
                        .subscribe(e -> log.info("{}", e)));
    }

    @Test
    public void groupBy() {
        Flux.range(1, 17)
                .groupBy(e -> e % 2 == 0 ? "Even" : "Odd")
                        .subscribe(groupedFlux ->
                                groupedFlux.collectList()
                                        .subscribe(data -> log.info("{}: {}", groupedFlux.key(), data)));

        log.info("---------------------------------------------");

        Flux.range(1, 17)
                .groupBy(e -> e % 2 == 0 ? "Even" : "Odd")
                .subscribe(groupedFlux -> groupedFlux
                        .scan(new LinkedList<>(),
                                (list, elem) -> {
                                    list.add(elem);
                                    if (list.size() > 2) {
                                        list.remove(0);
                                    }
                                    return list;
                                })
                        .filter(arr -> !arr.isEmpty())
                        .subscribe(data -> log.info("{}: {}", groupedFlux.key(), data)));
    }

    private boolean isPrime(int num) {
        if (num <= 1) {
            return false;
        }

        int n = 2;
        while (num % n != 0) {
            ++n;
        }
        return n == num;
    }
}
