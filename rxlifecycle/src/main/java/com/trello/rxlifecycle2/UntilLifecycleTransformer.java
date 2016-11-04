package com.trello.rxlifecycle2;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Continues a subscription until it sees *any* lifecycle event.
 */
@ParametersAreNonnullByDefault
final class UntilLifecycleTransformer<T, R> implements LifecycleTransformer<T> {

    final Observable<R> lifecycle;

    public UntilLifecycleTransformer(Observable<R> lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.takeUntil(lifecycle);
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream.takeUntil(lifecycle.take(1).singleOrError());
    }

    @Override
    public CompletableSource apply(Completable upstream) {
        return Completable.ambArray(
            upstream,
            lifecycle
                .flatMapCompletable(Functions.CANCEL_COMPLETABLE)
        );
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        UntilLifecycleTransformer<?, ?> that = (UntilLifecycleTransformer<?, ?>) o;

        return lifecycle.equals(that.lifecycle);
    }

    @Override
    public int hashCode() {
        return lifecycle.hashCode();
    }

    @Override
    public String toString() {
        return "UntilLifecycleTransformer{" +
            "lifecycle=" + lifecycle +
            '}';
    }
}
