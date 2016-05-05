package com.trello.rxlifecycle;

import rx.Observable;
import rx.Single;

/**
 * Continues a subscription until it sees *any* lifecycle event.
 */
class UntilLifecycleSingleTransformer<T, R> implements Single.Transformer<T, T> {

    final Observable<R> lifecycle;

    public UntilLifecycleSingleTransformer(Observable<R> lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public Single<T> call(Single<T> source) {
        return source.takeUntil(lifecycle);
    }
}
