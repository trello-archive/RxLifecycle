package com.trello.rxlifecycle2.navi;

import io.reactivex.functions.Predicate;

final class RxUtils {

    static <T> Predicate<T> notNull() {
        return new Predicate<T>() {
            @Override
            public boolean test(T t) throws Exception {
                return t != null;
            }
        };
    }

    private RxUtils() {
        throw new AssertionError("No instances!");
    }
}
