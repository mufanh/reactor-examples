package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ReactorPublishOnTest {

    @Test
    public void publishOn() {
        Scheduler scheduler1 = Schedulers.fromExecutor(Executors.newSingleThreadExecutor());
        Scheduler scheduler2 = Schedulers.fromExecutor(Executors.newSingleThreadExecutor());

        Flux.range(1, 100)
                .map(String::valueOf)
                .publishOn(scheduler1)
                .map(this::doBusinessLogic1)
                .publishOn(scheduler2)
                .map(this::doBusinessLogic2)
                .subscribe();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException ignored) {
        }
    }

    private String doBusinessLogic2(String s) {
        log.info("[B] {} in thread: {}", s, Thread.currentThread().getName());
        return s;
    }

    private String doBusinessLogic1(String s) {
        log.info("[A] {} in thread: {}", s, Thread.currentThread().getName());
        return s;
    }
}
