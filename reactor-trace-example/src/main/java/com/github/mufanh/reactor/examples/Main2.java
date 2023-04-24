package com.github.mufanh.reactor.examples;

import com.github.mufanh.reactor.examples.trace.ReactorTraceHooks;
import com.github.mufanh.reactor.examples.trace.TraceUtil;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@Slf4j
public class Main2 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try {
            ReactorTraceHooks.setHook();
            CompletableFuture<String> future = TraceUtil.toFuture(Mono.defer((Supplier<Mono<String>>) () -> {
                log.info("A");
                return Mono.just("A").doOnEach(e -> {
                    log.info("doOnEach:A");
                }).flatMap(e -> Mono.deferContextual(context -> {
                    log.info("trace_id:A:{}", context.getOrEmpty(TraceUtil.TRACE_ID));
                    return Mono.just(e);
                }));
            }).zipWith(Mono.just("B").doOnEach(e -> {
                log.info("doOnEach:B");
            }).flatMap(e -> Mono.deferContextual(context -> {
                log.info("trace_id:B:{}", context.getOrEmpty(TraceUtil.TRACE_ID));
                return Mono.just(e);
            })), (s, s2) -> {
                log.info("B");
                return s + ":" + s2;
            }).zipWith(Mono.just("C").doOnEach(e -> {
                log.info("doOnEach:C");
            }).flatMap(e -> Mono.deferContextual(context -> {
                log.info("trace_id:C:{}", context.getOrEmpty(TraceUtil.TRACE_ID));
                return Mono.just(e);
            })), (s, s2) -> {
                log.info("C");
                return s + ":" + s2;
            }));
            log.info("result:{}", future.get());
        } finally {
            ReactorTraceHooks.resetHook();
        }
    }
}
