package com.github.mufanh.reactor.examples.trace;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Slf4j
public final class TraceUtil {

    public static final String TRACE_ID = "_traceId";

    public static String PREFIX = "";

    static {
        String prefix = System.getProperty("trace.prefix");
        if (StringUtils.isNotBlank(prefix) && prefix.length() >= 2) {
            PREFIX = prefix.substring(0, 2);
        }
    }

    public static <T> CompletableFuture<T> toFuture(Mono<T> publisher) {
        return publisher.contextWrite(TraceUtil::injectReactorTraceIdFromMDC).toFuture();
    }

    public static <T> Mono<T> injectMonoTraceIdFromMDC(@NonNull Mono<T> publisher) {
        return publisher.contextWrite(TraceUtil::injectReactorTraceIdFromMDC);
    }

    public static <T> Flux<T> injectFluxTraceIdFromMDC(@NonNull Flux<T> publisher) {
        return publisher.contextWrite(TraceUtil::injectReactorTraceIdFromMDC);
    }

    public static Context injectReactorTraceIdFromMDC(Context context) {
        return context.put(TRACE_ID, getMDCTraceIdWithGenerateIfEmpty());
    }

    private static String generateTraceId() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        if (StringUtils.isNotBlank(PREFIX)) {
            return PREFIX + uuid.substring(PREFIX.length());
        }
        return uuid;
    }

    private static String getMDCTraceIdWithGenerateIfEmpty() {
        String traceId = getMDCTraceId();
        if (traceId == null) {
            traceId = generateTraceId();
            setMDCTraceId(traceId);
        }
        return traceId;
    }


    private static String getMDCTraceId() {
        return MDC.get(TRACE_ID);
    }

    private static void setMDCTraceId(String traceId) {
        if (StringUtils.isNotBlank(traceId)) {
            traceId = StringUtils.left(traceId, 36);
        }
        MDC.put(TRACE_ID, traceId);
    }

    private TraceUtil() {
    }
}
