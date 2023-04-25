package com.github.mufanh.reactor.examples;

import com.github.mufanh.reactor.examples.trace.ReactorTraceHooks;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
public class Main2 {

    public static void main(String[] args) {
        MDC.setContextMap(Collections.singletonMap("trace_id", UUID.randomUUID().toString()));
        try {
            ReactorTraceHooks.setHook();
            test();
        } finally {
            ReactorTraceHooks.resetHook();
        }
    }

    private static void test() {
        String result = Mono.defer((Supplier<Mono<String>>) () -> {
            log.info("A");
            return Mono.just("A").doOnEach(e -> {
                log.info("doOnEach:A");
            }).flatMap(e -> Mono.deferContextual(context -> {
                log.info("trace_id:A:{}", context.getOrEmpty("trace_id"));
                return Mono.just(e);
            }));
        }).zipWith(Mono.just("B").doOnEach(e -> {
            log.info("doOnEach:B");
        }).flatMap(e -> Mono.deferContextual(context -> {
            log.info("trace_id:B:{}", context.getOrEmpty("trace_id"));
            return Mono.just(e);
        })), (s, s2) -> {
            log.info("B");
            return s + ":" + s2;
        }).zipWith(Mono.just("C").doOnEach(e -> {
            log.info("doOnEach:C");
        }).flatMap(e -> Mono.deferContextual(context -> {
            log.info("trace_id:C:{}", context.getOrEmpty("trace_id"));
            return Mono.just(e);
        })), (s, s2) -> {
            log.info("C");
            return s + ":" + s2;
        }).block();
        log.info("result:{}", result);
    }
}
