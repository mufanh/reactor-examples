package com.github.mufanh.reactor.examples;

import lombok.Getter;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

public class CreateReactorModelTest {

    @Test
    public void createFlux() {
        Flux<String> stream1 = Flux.just("Hello", "world");
        printFluxElement("stream1", stream1);

        Flux<Integer> stream2 = Flux.fromArray(new Integer[]{1, 2, 3});
        printFluxElement("stream2", stream2);

        Flux<Integer> stream3 = Flux.fromIterable(Arrays.asList(9, 8, 7));
        printFluxElement("stream3", stream3);

        Flux<Integer> stream4 = Flux.range(2010, 9);
        printFluxElement("stream4", stream4);
    }

    @Test
    public void createMono() {
        Mono<String> stream1 = Mono.just("one");
        printMonoElement("stream1", stream1);

        Mono<String> stream2 = Mono.justOrEmpty(null);
        printMonoElement("stream2", stream2);

        Mono<String> stream3 = Mono.justOrEmpty(Optional.empty());
        printMonoElement("stream3", stream3);
    }

    @Test
    public void createSpecialModel() {
        Flux<String> empty = Flux.empty();
        printFluxElement("empty", empty);

        // never和empty的区别：empty有结束事件，never没有结束事件
        Flux<String> never = Flux.never();
        printFluxElement("never", never);

        Flux<String> error = Flux.error(new RuntimeException("Runtime Error"));

        System.out.println("error throw exception:");
        error.subscribe(e -> {}, Throwable::printStackTrace);
    }

    @Test
    public void createMonoWithCallable() {
        Mono<Long> stream1 = Mono.fromCallable(() -> {
            long startTime = System.currentTimeMillis();
            Thread.sleep(2000);
            return System.currentTimeMillis() - startTime;
        });

        long startTime = System.currentTimeMillis();
        printMonoElement("stream1", stream1);
        System.out.printf("cost: %.2f s\n", (System.currentTimeMillis() - startTime) / 1000.0);
    }

    @Test
    public void createDeferMono() {
        Mono<User> stream1 = Mono.fromCallable(() -> new User("stream1"));
        // 只有订阅的时候才会触发call的调用
        stream1.subscribe();
        stream1.subscribe();
        stream1.subscribe();

        Mono<User> stream2 = Mono.defer((Supplier<Mono<User>>)
                () -> Mono.just(new User("stream2")));
        // 只有订阅的时候才会触发创建
        stream2.subscribe();
        stream2.subscribe();
        stream2.subscribe();

        Mono<User> stream3 = Mono.just(new User("stream3"));
        stream3.subscribe();
        stream3.subscribe();
        stream3.subscribe();
    }

    private static void printMonoElement(String title, Mono<?> mono) {
        if (mono == null) {
            return;
        }
        System.out.printf("%s = ", title);
        mono.subscribe(data -> {
            System.out.print(data + " ");
        });
        System.out.println();
    }

    private static void printFluxElement(String title, Flux<?> flux) {
        if (flux == null) {
            return;
        }
        System.out.printf("%s = ", title);
        flux.subscribe(data -> {
            System.out.print(data + " ");
        });
        System.out.println();
    }



    @Getter
    private static class User {

        private final String name;

        private User(String name) {
            this.name = name;

            System.out.println("user [" + name + "] created, random:" + new Random().nextInt(100000));
        }
    }
}
