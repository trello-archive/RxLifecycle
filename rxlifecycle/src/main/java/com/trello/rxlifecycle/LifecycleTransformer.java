package com.trello.rxlifecycle;

import rx.Observable;
import rx.Single;

public interface LifecycleTransformer<T> extends Observable.Transformer<T, T> {

    Single.Transformer<T, T> forSingle();

}
