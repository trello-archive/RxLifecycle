package com.trello.rxlifecycle2;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import javax.annotation.Nonnull;

final class TakeUntilGenerator {

    @Nonnull
    static <T> Observable<T> takeUntilEvent(@Nonnull final Observable<T> lifecycle, @Nonnull final T event) {
        return lifecycle.filter(new Predicate<T>() {
            @Override
            public boolean test(T lifecycleEvent) throws Exception {
                return lifecycleEvent.equals(event);
            }
        }).take(1);
    }

    @Nonnull
    static <T> Observable<Boolean> takeUntilCorrespondingEvent(@Nonnull final Observable<T> lifecycle,
                                                               @Nonnull final Function<T, T> correspondingEvents) {
        return Observable.combineLatest(
            lifecycle.take(1).map(correspondingEvents),
            lifecycle.skip(1),
            new BiFunction<T, T, Boolean>() {
                @Override
                public Boolean apply(T bindUntilEvent, T lifecycleEvent) throws Exception {
                    return lifecycleEvent.equals(bindUntilEvent);
                }
            })
            .onErrorReturn(Functions.RESUME_FUNCTION)
            .filter(Functions.SHOULD_COMPLETE)
            .take(1);
    }

    private TakeUntilGenerator() {
        throw new AssertionError("No instances!");
    }
}
