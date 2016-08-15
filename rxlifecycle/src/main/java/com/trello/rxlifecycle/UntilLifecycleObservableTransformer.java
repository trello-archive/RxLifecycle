package com.trello.rxlifecycle;

import org.jetbrains.annotations.NotNull;
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Continues a subscription until it sees *any* lifecycle event.
 */
final class UntilLifecycleObservableTransformer<T, R> implements LifecycleTransformer<T> {

    final Observable<R> lifecycle;

    public UntilLifecycleObservableTransformer(@NotNull Observable<R> lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public Observable<T> call(Observable<T> source) {
        return source.takeUntil(lifecycle);
    }

    @NotNull
    @Override
    public Single.Transformer<T, T> forSingle() {
        return new UntilLifecycleSingleTransformer<>(lifecycle);
    }

    @NotNull
    @Override
    public Completable.CompletableTransformer forCompletable() {
        return new UntilLifecycleCompletableTransformer<>(lifecycle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        UntilLifecycleObservableTransformer<?, ?> that = (UntilLifecycleObservableTransformer<?, ?>) o;

        return lifecycle.equals(that.lifecycle);
    }

    @Override
    public int hashCode() {
        return lifecycle.hashCode();
    }

    @Override
    public String toString() {
        return "UntilLifecycleObservableTransformer{" +
            "lifecycle=" + lifecycle +
            '}';
    }
}
