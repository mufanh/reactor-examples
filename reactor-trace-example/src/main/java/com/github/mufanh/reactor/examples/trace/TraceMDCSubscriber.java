package com.github.mufanh.reactor.examples.trace;


import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.reactivestreams.Subscription;
import org.slf4j.MDC;
import reactor.core.CoreSubscriber;
import reactor.util.context.Context;

import java.util.Optional;


@Slf4j
public class TraceMDCSubscriber implements CoreSubscriber<Object> {
    private final String traceIdKey;

    private final CoreSubscriber<Object> actual;

    public TraceMDCSubscriber(CoreSubscriber<Object> actual, String traceIdKey) {
        if (StringUtils.isBlank(traceIdKey)) {
            throw new IllegalArgumentException("traceId key cannot be empty");
        }
        this.actual = Preconditions.checkNotNull(actual);
        this.traceIdKey = traceIdKey;
    }

    @Override
    public void onSubscribe(@NonNull Subscription s) {
        TraceUtil.injectReactorTraceIdFromMDC(actual.currentContext());
        actual.onSubscribe(s);
    }

    @Override
    public void onNext(Object o) {
        Context context = actual.currentContext();
        Optional<String> traceIdOptional = Optional.empty();
        if (!context.isEmpty() && context.hasKey(traceIdKey)) {
            traceIdOptional = context.getOrEmpty(traceIdKey);
        }
        try (MDC.MDCCloseable ignored = MDC.putCloseable(traceIdKey, traceIdOptional.orElse("N/A"))) {
            actual.onNext(o);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        actual.onError(throwable);
    }

    @Override
    public void onComplete() {
        actual.onComplete();
    }

    @NonNull
    @Override
    public Context currentContext() {
        return actual.currentContext();
    }
}