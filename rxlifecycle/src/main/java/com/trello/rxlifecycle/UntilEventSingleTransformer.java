package com.trello.rxlifecycle;

import rx.Observable;
import rx.Single;
import rx.functions.Func1;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 */
class UntilEventSingleTransformer<T, R> implements Single.Transformer<T, T> {

    final Observable<R> lifecycle;
    final R event;

    public UntilEventSingleTransformer(Observable<R> lifecycle, R event) {
        this.lifecycle = lifecycle;
        this.event = event;
    }

    @Override
    public Single<T> call(Single<T> source) {
        return source.takeUntil(
            lifecycle.takeFirst(new Func1<R, Boolean>() {
                @Override
                public Boolean call(R lifecycleEvent) {
                    return lifecycleEvent.equals(event);
                }
            })
        );
    }
}
