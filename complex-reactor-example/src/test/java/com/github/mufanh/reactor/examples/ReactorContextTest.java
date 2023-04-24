package com.github.mufanh.reactor.examples;

import org.junit.Test;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Supplier;

public class ReactorContextTest {

    @Test
    public void testContext() {
        Mono.just("hello")
                .flatMap(e ->Mono.deferContextual(context -> Mono.just(context.get("trace_id") + ":" + e)))
                .contextWrite(context -> context.put("trace_id", UUID.randomUUID().toString()))
                .subscribe(System.out::println);
    }

    @Test
    public void testContext2() {
        Mono.defer((Supplier<Mono<String>>) () -> Mono.just("hello")
                .flatMap(e ->Mono.deferContextual(
                        context -> Mono.just(context.get("trace_id") + ":" + e))))
                .flatMap(e ->Mono.deferContextual(context -> Mono.just(context.get("trace_id") + ":" + e)))
                .zipWith(Mono.just("world")
                        .flatMap(e -> Mono.deferContextual(
                                context -> Mono.just(context.get("trace_id") + ":" + e))), (s1, s2) -> s1 + " " + s2)
                .flatMap(e ->Mono.deferContextual(context -> Mono.just(context.get("trace_id") + ":" + e)))
                .contextWrite(context -> context.put("trace_id", "A"))
                .subscribe(System.out::println);
    }
}
