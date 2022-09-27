package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
public class ReactorContextTest {

    @Test
    public void useThreadLocal() {
        ThreadLocal<Map<Object, Object>> threadLocal = new ThreadLocal<>();
        threadLocal.set(new HashMap<>());

        Flux.range(0, 10)
                .doOnNext(k -> threadLocal.get().put(k, new Random(k).nextGaussian()))
                //.publishOn(Schedulers.parallel()) // 切换线程了必然NPE
                .map(k -> threadLocal.get().get(k))
                .blockLast();
    }

    @Test
    public void context() {
        Flux.range(0, 10)
                .flatMap(k -> Mono.subscriberContext()
                        .doOnNext(context -> {
                            Map<Object, Object> map = context.get("random");
                            map.put(k, new Random(k).nextGaussian());
                        })
                        .thenReturn(k))
                .publishOn(Schedulers.parallel())
                .flatMap(k -> Mono.subscriberContext()
                        .map(context -> {
                            Map<Object, Object> map = context.get("random");
                            return map.get(k);
                        }))
                .subscriberContext(context -> context.put("random", new HashMap<>()))
                .blockLast();
    }
}
