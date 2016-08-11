package com.trello.rxlifecycle;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * A Transformer that works for all RxJava types ({@link Observable}, {@link Single} and {@link Completable}).
 *
 * Out of the box, it works for Observable. But it can be easily converted
 * for {@link Single} or for {@link Completable}.
 */
public interface LifecycleTransformer<T> extends Observable.Transformer<T, T> {

    /**
     * @return a version of this Transformer for {@link Single} streams.
     *
     * If interrupted by the lifecycle, this stream throws onError({@link java.util.concurrent.CancellationException}).
     */
    @CheckResult
    @NonNull
    // Implementation note: We use a different generic to cover some insane case in Java 8 inference.
    // See more here: https://github.com/trello/RxLifecycle/issues/126
    <U> Single.Transformer<U, U> forSingle();

    /**
     * @return a version of this Transformer for {@link Completable} streams.
     *
     * If interrupted by the lifecycle, this stream throws onError({@link java.util.concurrent.CancellationException}).
     */
    @CheckResult
    @NonNull
    Completable.CompletableTransformer forCompletable();

}
