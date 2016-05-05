package com.trello.rxlifecycle;

import rx.Observable;

/**
 * Continues a subscription until it sees *any* lifecycle event.
 */
class UntilLifecycleObservableTransformer<T, R> implements Observable.Transformer<T, T> {

    final Observable<R> lifecycle;

    public UntilLifecycleObservableTransformer(Observable<R> lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public Observable<T> call(Observable<T> source) {
        return source.takeUntil(lifecycle);
    }
}
