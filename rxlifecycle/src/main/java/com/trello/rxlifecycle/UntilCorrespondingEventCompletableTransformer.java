package com.trello.rxlifecycle;

import rx.Completable;
import rx.Observable;
import rx.functions.Func1;

import javax.annotation.Nonnull;

import static com.trello.rxlifecycle.TakeUntilGenerator.takeUntilCorrespondingEvent;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 *
 * That lifecycle event is determined based on what stage we're at in
 * the current lifecycle.
 */
final class UntilCorrespondingEventCompletableTransformer<T> implements Completable.CompletableTransformer {

    final Observable<T> sharedLifecycle;
    final Func1<T, T> correspondingEvents;

    public UntilCorrespondingEventCompletableTransformer(@Nonnull Observable<T> sharedLifecycle,
                                                         @Nonnull Func1<T, T> correspondingEvents) {
        this.sharedLifecycle = sharedLifecycle;
        this.correspondingEvents = correspondingEvents;
    }

    @Override
    public Completable call(Completable source) {
        return Completable.amb(
            source,
            takeUntilCorrespondingEvent(sharedLifecycle, correspondingEvents)
                .flatMap(Functions.CANCEL_COMPLETABLE)
                .toCompletable()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        UntilCorrespondingEventCompletableTransformer<?> that = (UntilCorrespondingEventCompletableTransformer<?>) o;

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
        return "UntilCorrespondingEventCompletableTransformer{" +
            "sharedLifecycle=" + sharedLifecycle +
            ", correspondingEvents=" + correspondingEvents +
            '}';
    }
}
