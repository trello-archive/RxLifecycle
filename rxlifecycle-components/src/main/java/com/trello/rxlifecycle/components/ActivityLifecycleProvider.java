package com.trello.rxlifecycle.components;

import com.trello.rxlifecycle.ActivityEvent;
import rx.Observable;

/**
 * Common interface for all RxActivity extensions.
 *
 * Useful if you are writing utilities on top of rxlifecycle-components,
 * or you are implementing your own component not supported in this library.
 */
public interface ActivityLifecycleProvider {

    Observable<ActivityEvent> lifecycle();

    <T> Observable.Transformer<T, T> bindUntilEvent(ActivityEvent event);

    <T> Observable.Transformer<T, T> bindToLifecycle();

}
