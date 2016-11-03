package com.trello.rxlifecycle;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

import static com.trello.rxlifecycle.TakeUntilGenerator.takeUntilEvent;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 */
@ParametersAreNonnullByDefault
final class UntilEventTransformer<T, R> implements LifecycleTransformer<T> {

    private final Observable<R> lifecycle;
    private final R event;

    public UntilEventTransformer(Observable<R> lifecycle, R event) {
        this.lifecycle = lifecycle;
        this.event = event;
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.takeUntil(takeUntilEvent(lifecycle, event));
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream.takeUntil(takeUntilEvent(lifecycle, event).singleOrError());
    }

    @Override
    public CompletableSource apply(Completable upstream) {
        return Completable.amb(
            Arrays.asList(
                upstream,
                takeUntilEvent(lifecycle, event)
                    .flatMap(Functions.CANCEL_COMPLETABLE)
                    .ignoreElements()
            )
        );
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        UntilEventTransformer<?, ?> that = (UntilEventTransformer<?, ?>) o;

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
        return "UntilEventTransformer{" +
            "lifecycle=" + lifecycle +
            ", event=" + event +
            '}';
    }
}
