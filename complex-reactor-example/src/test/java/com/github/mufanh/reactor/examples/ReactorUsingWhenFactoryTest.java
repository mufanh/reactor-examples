package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;

@Slf4j
public class ReactorUsingWhenFactoryTest {

    @Test
    public void usingWhen() {
        Flux.usingWhen(
                Transaction.beginTransaction(),
                transaction -> transaction.insertRows(Flux.just("A", "B", "C")),
                Transaction::commit,
                (transaction, throwable) -> {
                    transaction.rollback();
                    log.info("error: {}", throwable.getMessage());
                    return Mono.empty();
                },
                Transaction::rollback
        ).subscribe(
                e -> log.info("onNext: {}", e),
                e -> log.error("onError: {}", e.getMessage()),
                () -> log.info("onComplete")
        );

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
    }

    private static class Transaction {

        private static final Random random = new Random();

        private final int id;

        public Transaction(int id) {
            this.id = id;
            log.info("[T: {}]", id);
        }

        public static Mono<Transaction> beginTransaction() {
            return Mono.defer(() -> Mono.just(new Transaction(random.nextInt(1000))));
        }

        public Flux<String> insertRows(Publisher<String> rows) {
            return Flux.from(rows)
                    .delayElements(Duration.ofMillis(100))
                    .flatMap(r -> {
                        if (random.nextInt(10) < 2) {
                            return Mono.error(new RuntimeException("Error:" + r));
                        } else {
                            return Mono.just(r);
                        }
                    });
        }

        public Mono<Void> commit() {
            return Mono.defer(() -> {
                log.info("[T: {}] commit", id);
                if (random.nextBoolean()) {
                    return Mono.empty();
                }
                return Mono.error(new RuntimeException("Conflict"));
            });
        }

        public Mono<Void> rollback() {
            return Mono.defer(() -> {
                log.info("[T: {}] rollback", id);
                if (random.nextBoolean()) {
                    return Mono.empty();
                }
                return Mono.error(new RuntimeException("Conn error"));
            });
        }
    }
}
