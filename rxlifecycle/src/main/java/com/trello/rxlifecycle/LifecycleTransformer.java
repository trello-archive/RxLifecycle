package com.trello.rxlifecycle;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * A Transformer that works for all RxJava types (Observable, Single and Comletable).
 *
 * Out of the box, it works for Observable. But it can be easily converted
 * for Single or for Completable.
 */
public interface LifecycleTransformer<T> extends Observable.Transformer<T, T> {

    /**
     * @return a version of this Transformer for Single streams.
     *
     * If interrupted by the lifecycle, this stream throws onError(CancellationException).
     */
    @CheckResult
    @NonNull
    Single.Transformer<T, T> forSingle();

    /**
     * @return a version of this Transformer for Completable streams.
     *
     * If interrupted by the lifecycle, this stream throws onError(CancellationException).
     */
    @CheckResult
    @NonNull
    Completable.CompletableTransformer forCompletable();

}
