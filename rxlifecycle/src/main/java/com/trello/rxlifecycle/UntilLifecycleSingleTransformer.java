package com.trello.rxlifecycle;

import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.Single;

/**
 * Continues a subscription until it sees *any* lifecycle event.
 */
final class UntilLifecycleSingleTransformer<T, R> implements Single.Transformer<T, T> {

    final Observable<R> lifecycle;

    public UntilLifecycleSingleTransformer(@NotNull Observable<R> lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public Single<T> call(Single<T> source) {
        return source.takeUntil(lifecycle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        UntilLifecycleSingleTransformer<?, ?> that = (UntilLifecycleSingleTransformer<?, ?>) o;

        return lifecycle.equals(that.lifecycle);
    }

    @Override
    public int hashCode() {
        return lifecycle.hashCode();
    }

    @Override
    public String toString() {
        return "UntilLifecycleSingleTransformer{" +
            "lifecycle=" + lifecycle +
            '}';
    }
}
