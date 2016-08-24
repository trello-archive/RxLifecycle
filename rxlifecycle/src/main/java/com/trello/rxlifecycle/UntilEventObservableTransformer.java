package com.trello.rxlifecycle;

import rx.Completable;
import rx.Observable;
import rx.Single;

import javax.annotation.Nonnull;

import static com.trello.rxlifecycle.TakeUntilGenerator.takeUntilEvent;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 */
final class UntilEventObservableTransformer<T, R> implements LifecycleTransformer<T> {

    final Observable<R> lifecycle;
    final R event;

    public UntilEventObservableTransformer(@Nonnull Observable<R> lifecycle, @Nonnull R event) {
        this.lifecycle = lifecycle;
        this.event = event;
    }

    @Override
    public Observable<T> call(Observable<T> source) {
        return source.takeUntil(takeUntilEvent(lifecycle, event));
    }

    @Nonnull
    @Override
    public Single.Transformer<T, T> forSingle() {
        return new UntilEventSingleTransformer<>(lifecycle, event);
    }

    @Nonnull
    @Override
    public Completable.CompletableTransformer forCompletable() {
        return new UntilEventCompletableTransformer<>(lifecycle, event);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        UntilEventObservableTransformer<?, ?> that = (UntilEventObservableTransformer<?, ?>) o;

        if (!lifecycle.equals(that.lifecycle)) { return false; }
        return event.equals(that.event);
    }

    @Override
    public int hashCode() {
        int result = lifecycle.hashCode();
        result = 31 * result + event.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UntilEventObservableTransformer{" +
            "lifecycle=" + lifecycle +
            ", event=" + event +
            '}';
    }
}
