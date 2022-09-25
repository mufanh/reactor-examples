package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

@Slf4j
public class ConnectableFluxTest {

    @Test
    public void connectable() {
        // 冷发布者
        Flux<Integer> source = Flux.range(0, 3)
                .doOnSubscribe(s -> log.info("new subscription for the cold publisher"));

        // 已经转成热发布者
        ConnectableFlux<Integer> conn = source.publish();
        conn.subscribe(s -> log.info("[Subscriber 1] onNext: {}", s));
        conn.subscribe(s -> log.info("[Subscriber 2] onNext: {}", s));

        log.info("all subscribers are ready, connecting");
        conn.connect();
    }

    @Test
    public void notConnectable() {
        Flux<Integer> source = Flux.range(0, 3)
                .doOnSubscribe(s -> log.info("new subscription for the cold publisher"));
        // 每次订阅，会生成一个新的流
        source.subscribe(s -> log.info("[Subscriber 1] onNext: {}", s));
        // 每次订阅，会生成一个新的流
        source.subscribe(s -> log.info("[Subscriber 2] onNext: {}", s));
    }
}
