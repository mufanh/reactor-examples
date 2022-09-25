package com.github.mufanh.reactor.examples;

import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

public class MySubscriberTest {

    @Test
    public void testMySubscriber() {
        Flux<String> stream = Flux.just("Hello", "world", "!");
        stream.subscribe(new MySubscriber<>());
    }

    private static class MySubscriber<T> extends BaseSubscriber<T> {

        @Override
        public void hookOnSubscribe(Subscription subscription) {
            System.out.println("init request for 1 element");
            request(1);
        }

        @Override
        public void hookOnNext(T t) {
            System.out.printf("onNext: %s\n", t);
            System.out.println("requesting 1 more element");
            request(1);
        }
    }
}
