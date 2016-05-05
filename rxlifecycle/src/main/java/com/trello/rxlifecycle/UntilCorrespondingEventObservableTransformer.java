package com.trello.rxlifecycle;

import rx.Observable;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.functions.Func2;

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
        return source.takeUntil(
            Observable.combineLatest(
                this.sharedLifecycle.take(1).map(correspondingEvents),
                this.sharedLifecycle.skip(1),
                new Func2<R, R, Boolean>() {
                    @Override
                    public Boolean call(R bindUntilEvent, R lifecycleEvent) {
                        return lifecycleEvent.equals(bindUntilEvent);
                    }
                })
                .onErrorReturn(RESUME_FUNCTION)
                .takeFirst(SHOULD_COMPLETE)
        );
    }

    private static final Func1<Throwable, Boolean> RESUME_FUNCTION = new Func1<Throwable, Boolean>() {
        @Override
        public Boolean call(Throwable throwable) {
            if (throwable instanceof OutsideLifecycleException) {
                return true;
            }

            Exceptions.propagate(throwable);
            return false;
        }
    };

    private static final Func1<Boolean, Boolean> SHOULD_COMPLETE = new Func1<Boolean, Boolean>() {
        @Override
        public Boolean call(Boolean shouldComplete) {
            return shouldComplete;
        }
    };
}
