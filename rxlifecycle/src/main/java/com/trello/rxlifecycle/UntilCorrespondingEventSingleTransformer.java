package com.trello.rxlifecycle;

import rx.Observable;
import rx.Single;
import rx.functions.Func1;

import static com.trello.rxlifecycle.TakeUntilGenerator.takeUntilCorrespondingEvent;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 *
 * That lifecycle event is determined based on what stage we're at in
 * the current lifecycle.
 */
class UntilCorrespondingEventSingleTransformer<T, R> implements Single.Transformer<T, T> {

    final Observable<R> sharedLifecycle;
    final Func1<R, R> correspondingEvents;

    public UntilCorrespondingEventSingleTransformer(Observable<R> lifecycle, Func1<R, R> correspondingEvents) {
        this.sharedLifecycle = lifecycle.share(); // Share so that we always compare identical lifecycles
        this.correspondingEvents = correspondingEvents;
    }

    @Override
    public Single<T> call(Single<T> source) {
        return source.takeUntil(takeUntilCorrespondingEvent(sharedLifecycle, correspondingEvents));
    }
}
