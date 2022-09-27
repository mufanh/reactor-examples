package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ReactorSubscribeOnTest {

    @Test
    public void subscribe() {
        Scheduler scheduler1 = Schedulers.fromExecutor(
                Executors.newSingleThreadExecutor(new NamedThreadFactory("scheduler1")));
        Scheduler scheduler2 = Schedulers.fromExecutor(
                Executors.newSingleThreadExecutor(new NamedThreadFactory("scheduler2")));

        Mono.fromCallable(() -> {
                    log.info("[fromCallback] in thread: {}", Thread.currentThread().getName());
                    return new Random().nextBoolean();
                })
                .map(e -> {
                    log.info("[map1] in thread: {}", Thread.currentThread().getName());
                    return e;
                })
                .publishOn(scheduler1)
                .map(e -> {
                    log.info("[map2] in thread: {}", Thread.currentThread().getName());
                    return e;
                })
                .subscribeOn(scheduler2)
                .map(e -> {
                    log.info("[map3] in thread: {}", Thread.currentThread().getName());
                    return e;
                })
                .subscribe();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException ignored) {
        }
    }
}
