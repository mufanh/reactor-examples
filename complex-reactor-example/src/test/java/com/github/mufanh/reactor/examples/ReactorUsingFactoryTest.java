package com.github.mufanh.reactor.examples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Random;

@Slf4j
public class ReactorUsingFactoryTest {

    @Test
    public void connection() {
        try (Connection conn = Connection.newConnection()) {
            conn.getData().forEach(e -> log.info("Received data: {}", e));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    @Test
    public void using() {
        Flux<String> ioRequestResults = Flux.using(
                Connection::newConnection,
                connection -> Flux.fromIterable(connection.getData()),
                Connection::close
        );
        ioRequestResults.subscribe(
                data -> log.info("Receive data: {}", data),
                e -> log.error("Error: {}", e.getMessage()),
                () -> log.info("Stream finished")
        );
    }

    private static class Connection implements AutoCloseable {

        private final Random random = new Random();

        public Iterable<String> getData() {
            if (random.nextInt(10) < 3) {
                throw new RuntimeException("Communication error");
            }
            return Arrays.asList("Some", "data");
        }

        public void close() {
            log.info("IO Connection closed");
        }

        public static Connection newConnection() {
            log.info("IO Connection created");
            return new Connection();
        }
    }
}
