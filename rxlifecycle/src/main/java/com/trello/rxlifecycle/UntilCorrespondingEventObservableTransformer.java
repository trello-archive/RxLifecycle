package com.trello.rxlifecycle;

import rx.Observable;
import rx.functions.Func1;

import static com.trello.rxlifecycle.TakeUntilGenerator.takeUntilCorrespondingEvent;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 *
 * That lifecycle event is determined based on what stage we're at in
 * the current lifecycle.
 */
class UntilCorrespondingEventObservableTransformer<T, R> implements Observable.Transformer<T, T> {

    final Observable<R> sharedLifecycle;
    final Func1<R, R> correspondingEvents;

    public UntilCorrespondingEventObservableTransformer(Observable<R> lifecycle, Func1<R, R> correspondingEvents) {
        this.sharedLifecycle = lifecycle.share(); // Share so that we always compare identical lifecycles
        this.correspondingEvents = correspondingEvents;
    }

    @Override
    public Observable<T> call(Observable<T> source) {
        return source.takeUntil(takeUntilCorrespondingEvent(sharedLifecycle, correspondingEvents));
    }
}
