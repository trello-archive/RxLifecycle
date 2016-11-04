package com.trello.rxlifecycle2;

import io.reactivex.CompletableTransformer;
import io.reactivex.FlowableTransformer;
import io.reactivex.MaybeTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;

/**
 * A Transformer that works for all RxJava types.
 */
public interface LifecycleTransformer<T> extends ObservableTransformer<T, T>,
                                                 FlowableTransformer<T, T>,
                                                 SingleTransformer<T, T>,
                                                 MaybeTransformer<T, T>,
                                                 CompletableTransformer
{

}
