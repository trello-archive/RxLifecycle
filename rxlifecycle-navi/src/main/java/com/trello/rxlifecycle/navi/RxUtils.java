package com.trello.rxlifecycle.navi;

import rx.functions.Func1;

final class RxUtils {

    static <T> Func1<T, Boolean> notNull() {
        return new Func1<T, Boolean>() {
            @Override
            public Boolean call(T t) {
                return t != null;
            }
        };
    }

    private RxUtils() {
        throw new AssertionError("No instances!");
    }
}
