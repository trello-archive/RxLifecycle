package com.trello.rxlifecycle2;

import io.reactivex.Completable;
import io.reactivex.CompletableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleTransformer;

/**
 * A Transformer that works for all RxJava types ({@link Observable}, {@link Single} and {@link Completable}).
 */
public interface LifecycleTransformer<T> extends ObservableTransformer<T, T>,
                                                 SingleTransformer<T, T>,
                                                 CompletableTransformer
{

}
