package com.github.mufanh.reactor.examples;

import com.github.mufanh.reactor.examples.trace.ReactorTraceHooks;
import com.github.mufanh.reactor.examples.trace.TraceUtil;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Slf4j
public class Main {

    public static void main(String[] args) {
        try {
            ReactorTraceHooks.setHook();
            test1();
            test2();
        } finally {
            ReactorTraceHooks.resetHook();
        }
    }

    private static void test1() {
        log.info("-----------------------------------------------------------------------");
        Mono<String> testMono = Mono.defer((Supplier<Mono<String>>) () -> {
            log.info("A");
            return Mono.just("A").doOnEach(e -> {
                log.info("doOnEach:A");
            }).flatMap(e -> Mono.deferContextual(context -> {
                log.info("trace_id:A:{}", context.getOrEmpty(TraceUtil.TRACE_ID));
                return Mono.just(e);
            }));
        });
        testMono = enrichMono(testMono, Mono.just("B").doOnEach(e -> {
            log.info("doOnEach:B");
        }).flatMap(e -> Mono.deferContextual(context -> {
            log.info("trace_id:B:{}", context.getOrEmpty(TraceUtil.TRACE_ID));
            return Mono.just(e);
        })), (s, s2) -> {
            log.info("B");
            return s + ":" + s2;
        });
        testMono = enrichMono(testMono, Mono.just("C").doOnEach(e -> {
            log.info("doOnEach:C");
        }).flatMap(e -> Mono.deferContextual(context -> {
            log.info("trace_id:C:{}", context.getOrEmpty(TraceUtil.TRACE_ID));
            return Mono.just(e);
        })), (s, s2) -> {
            log.info("C");
            return s + ":" + s2;
        });
        testMono = testMono.contextWrite(context -> context.put(TraceUtil.TRACE_ID, UUID.randomUUID().toString()));
        testMono.block();
    }

    private static void test2() {
        log.info("-----------------------------------------------------------------------");
        Mono.defer((Supplier<Mono<String>>) () -> {
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
                }).contextWrite(context -> context.put(TraceUtil.TRACE_ID, UUID.randomUUID().toString()))
                .block();
    }

    private static <RESULT, T> Mono<RESULT> enrichMono(Mono<RESULT> resultMono, Mono<T> objMono, BiFunction<RESULT, T, RESULT> enrichFun) {
        Preconditions.checkNotNull(resultMono);
        Preconditions.checkNotNull(objMono);
        return resultMono.zipWith(objMono, enrichFun);
    }
}
