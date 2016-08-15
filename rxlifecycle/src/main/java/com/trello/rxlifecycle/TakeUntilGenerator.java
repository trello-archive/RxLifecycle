package com.trello.rxlifecycle;

import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

final class TakeUntilGenerator {

    @NotNull
    static <T> Observable<T> takeUntilEvent(@NotNull final Observable<T> lifecycle, @NotNull final T event) {
        return lifecycle.takeFirst(new Func1<T, Boolean>() {
            @Override
            public Boolean call(T lifecycleEvent) {
                return lifecycleEvent.equals(event);
            }
        });
    }

    @NotNull
    static <T> Observable<Boolean> takeUntilCorrespondingEvent(@NotNull final Observable<T> lifecycle,
                                                               @NotNull final Func1<T, T> correspondingEvents) {
        return Observable.combineLatest(
            lifecycle.take(1).map(correspondingEvents),
            lifecycle.skip(1),
            new Func2<T, T, Boolean>() {
                @Override
                public Boolean call(T bindUntilEvent, T lifecycleEvent) {
                    return lifecycleEvent.equals(bindUntilEvent);
                }
            })
            .onErrorReturn(Functions.RESUME_FUNCTION)
            .takeFirst(Functions.SHOULD_COMPLETE);
    }

    private TakeUntilGenerator() {
        throw new AssertionError("No instances!");
    }
}
