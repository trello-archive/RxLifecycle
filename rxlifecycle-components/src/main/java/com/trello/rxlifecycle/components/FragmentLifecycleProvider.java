package com.trello.rxlifecycle.components;

import com.trello.rxlifecycle.FragmentEvent;
import rx.Observable;

/**
 * Common interface for all RxFragment extensions.
 *
 * Useful if you are writing utilities on top of rxlifecycle-components,
 * or you are implementing your own component not supported in this library.
 */
public interface FragmentLifecycleProvider {

    Observable<FragmentEvent> lifecycle();

    <T> Observable.Transformer<T, T> bindUntilEvent(FragmentEvent event);

    <T> Observable.Transformer<T, T> bindToLifecycle();

}
