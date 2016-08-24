package com.trello.rxlifecycle;

import rx.Completable;
import rx.Observable;

import javax.annotation.Nonnull;

import static com.trello.rxlifecycle.TakeUntilGenerator.takeUntilEvent;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 */
final class UntilEventCompletableTransformer<T> implements Completable.CompletableTransformer {

    final Observable<T> lifecycle;
    final T event;

    public UntilEventCompletableTransformer(@Nonnull Observable<T> lifecycle, @Nonnull T event) {
        this.lifecycle = lifecycle;
        this.event = event;
    }

    @Override
    public Completable call(Completable source) {
        return Completable.amb(
            source,
            takeUntilEvent(lifecycle, event)
                .flatMap(Functions.CANCEL_COMPLETABLE)
                .toCompletable()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        UntilEventCompletableTransformer<?> that = (UntilEventCompletableTransformer<?>) o;

        if (!lifecycle.equals(that.lifecycle)) { return false; }
        return event.equals(that.event);
    }

    @Override
    public int hashCode() {
        int result = lifecycle.hashCode();
        result = 31 * result + event.hashCode();
        return result;
    }

}
