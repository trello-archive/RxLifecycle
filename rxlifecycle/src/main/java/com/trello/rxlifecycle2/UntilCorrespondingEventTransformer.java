package com.trello.rxlifecycle2;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.trello.rxlifecycle2.TakeUntilGenerator.takeUntilCorrespondingEvent;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 *
 * That lifecycle event is determined based on what stage we're at in
 * the current lifecycle.
 */
@ParametersAreNonnullByDefault
final class UntilCorrespondingEventTransformer<T, R> implements LifecycleTransformer<T> {

    private final Observable<R> sharedLifecycle;
    private final Function<R, R> correspondingEvents;

    public UntilCorrespondingEventTransformer(Observable<R> sharedLifecycle,
                                              Function<R, R> correspondingEvents) {
        this.sharedLifecycle = sharedLifecycle;
        this.correspondingEvents = correspondingEvents;
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.takeUntil(takeUntilCorrespondingEvent(sharedLifecycle, correspondingEvents));
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream.takeUntil(takeUntilCorrespondingEvent(sharedLifecycle, correspondingEvents).singleOrError());
    }

    @Override
    public CompletableSource apply(Completable upstream) {
        return Completable.ambArray(
            upstream,
            takeUntilCorrespondingEvent(sharedLifecycle, correspondingEvents)
                .flatMapCompletable(Functions.CANCEL_COMPLETABLE)
        );
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        UntilCorrespondingEventTransformer<?, ?> that = (UntilCorrespondingEventTransformer<?, ?>) o;

        if (!sharedLifecycle.equals(that.sharedLifecycle)) { return false; }
        return correspondingEvents.equals(that.correspondingEvents);
    }

    @Override
    public int hashCode() {
        int result = sharedLifecycle.hashCode();
        result = 31 * result + correspondingEvents.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UntilCorrespondingEventTransformer{" +
            "sharedLifecycle=" + sharedLifecycle +
            ", correspondingEvents=" + correspondingEvents +
            '}';
    }
}
