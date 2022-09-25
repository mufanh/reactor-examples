package com.github.mufanh.reactor.examples;

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class SubscribeReactorModelTest {

    @Test
    public void subscriptionCancel() {
        Flux.range(1, 1000)
                .subscribe(
                        data -> System.out.printf("onNext: %s\n", data),
                        err -> {},
                        () -> System.out.println("onComplete"),
                        subscription -> {
                            subscription.request(4);
                            subscription.cancel();
                        }
                );
    }

    @Test
    public void disposableDispose() {
        Disposable disposable = Flux.interval(Duration.ofMillis(50))
                .subscribe(data -> System.out.printf("onNext: %s\n", data));
        try {
            Thread.sleep(200);
        } catch (InterruptedException ignored) {
        }
        disposable.dispose();
    }

    @Test
    public void customSubscribe() {
        Subscriber<String> subscriber = new Subscriber<String>() {

            volatile Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                System.out.println("init request for 1 element");
                subscription.request(1);
            }

            @Override
            public void onNext(String s) {
                System.out.printf("onNext: %s\n", s);
                System.out.println("requesting 1 more element");
                subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError:");
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete:");
            }
        };

        Flux<String> stream = Flux.just("Hello", "world", "!");
        stream.subscribe(subscriber);
    }
}
