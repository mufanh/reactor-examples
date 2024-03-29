package com.github.mufanh.reactor.examples;

import com.github.mufanh.reactor.examples.trace.ReactorTraceHooks;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Slf4j
public class Main {

    public static void main(String[] args) {
        try {
            ReactorTraceHooks.setHook();
            MDC.setContextMap(Collections.singletonMap("trace_id", UUID.randomUUID().toString()));
            test1();
            test2();
            test3();
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
                log.info("trace_id:A:{}", context.getOrEmpty("trace_id"));
                return Mono.just(e);
            }));
        });
        testMono = enrichMono(testMono, Mono.just("B").doOnEach(e -> {
            log.info("doOnEach:B");
        }).flatMap(e -> Mono.deferContextual(context -> {
            log.info("trace_id:B:{}", context.getOrEmpty("trace_id"));
            return Mono.just(e);
        })), (s, s2) -> {
            log.info("B");
            return s + ":" + s2;
        });
        testMono = enrichMono(testMono, Mono.just("C").doOnEach(e -> {
            log.info("doOnEach:C");
        }).flatMap(e -> Mono.deferContextual(context -> {
            log.info("trace_id:C:{}", context.getOrEmpty("trace_id"));
            return Mono.just(e);
        })), (s, s2) -> {
            log.info("C");
            return s + ":" + s2;
        });
        testMono.block();
    }

    private static void test2() {
        log.info("-----------------------------------------------------------------------");
        Mono.defer((Supplier<Mono<String>>) () -> {
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
                })
                .contextWrite(context -> context.put("trace_id", UUID.randomUUID().toString()))
                .block();
    }

    private static void test3() {
        log.info("-----------------------------------------------------------------------");
        Mono.error(new RuntimeException("测试异常"))
                .doOnError(e -> {
                    log.error("运行时异常1", e);
                })
                .onErrorResume(e -> {
                    log.error("运行时异常2", e);
                    return Mono.just("A").doOnEach(e1 -> {
                        log.info("doOnEach:A");
                    }).flatMap(e2 -> Mono.deferContextual(context -> {
                        log.info("trace_id:A:{}", context.getOrEmpty("trace_id"));
                        return Mono.just(e2);
                    }));
                })
                .zipWith(Mono.just("B").doOnEach(e -> {
                    log.info("doOnEach:B");
                }).flatMap(e -> Mono.deferContextual(context -> {
                    log.info("trace_id:B:{}",  context.getOrEmpty("trace_id"));
                    return Mono.just(e);
                })), (s, s2) -> {
                    log.info("B");
                    return s + ":" + s2;
                }).zipWith(Mono.just("C").doOnEach(e -> {
                    log.info("doOnEach:C");
                }).flatMap(e -> Mono.deferContextual(context -> {
                    log.info("trace_id:C:{}",  context.getOrEmpty("trace_id"));
                    return Mono.just(e);
                })), (s, s2) -> {
                    log.info("C");
                    return s + ":" + s2;
                })
                .contextWrite(context -> context.put("trace_id", UUID.randomUUID().toString()))
                .block();
    }

    private static <RESULT, T> Mono<RESULT> enrichMono(Mono<RESULT> resultMono, Mono<T> objMono, BiFunction<RESULT, T, RESULT> enrichFun) {
        Preconditions.checkNotNull(resultMono);
        Preconditions.checkNotNull(objMono);
        return resultMono.zipWith(objMono, enrichFun);
    }
}
