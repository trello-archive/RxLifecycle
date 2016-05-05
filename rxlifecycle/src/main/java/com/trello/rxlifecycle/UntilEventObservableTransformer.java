package com.trello.rxlifecycle;

import rx.Observable;

import static com.trello.rxlifecycle.TakeUntilGenerator.takeUntilEvent;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 */
class UntilEventObservableTransformer<T, R> implements Observable.Transformer<T, T> {

    final Observable<R> lifecycle;
    final R event;

    public UntilEventObservableTransformer(Observable<R> lifecycle, R event) {
        this.lifecycle = lifecycle;
        this.event = event;
    }

    @Override
    public Observable<T> call(Observable<T> source) {
        return source.takeUntil(takeUntilEvent(lifecycle, event));
    }
}
