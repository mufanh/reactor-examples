package com.github.mufanh.reactor.examples.trace;

import reactor.core.publisher.Operators;

public class ReactorTraceHooks {

    private static final String HOOK_KEY = "logBpdMdc";

    public static void setHook() {
        reactor.core.publisher.Hooks.onEachOperator(HOOK_KEY,
                Operators.lift((scannable, coreSubscriber) -> new TraceMDCSubscriber(coreSubscriber, TraceUtil.TRACE_ID)));
    }

    public static void resetHook() {
        reactor.core.publisher.Hooks.resetOnEachOperator(HOOK_KEY);
    }

}
