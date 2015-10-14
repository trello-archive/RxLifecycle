package com.trello.rxlifecycle.components;

import com.trello.rxlifecycle.ServiceEvent;

import rx.Observable;

/**
 * Common interface for all RxService extensions.
 *
 * Useful if you are writing utilities on top of rxlifecycle-components,
 * or you are implementing your own component not supported in this library.
 */
public interface ServiceLifecycleProvider {

    /**
     * @return a sequence of {@link android.app.Activity} lifecycle events
     */
    Observable<ServiceEvent> lifecycle();

    /**
     * Binds a source until a specific {@link ServiceEvent} occurs.
     * <p>
     * Intended for use with {@link Observable#compose(Observable.Transformer)}
     *
     * @param event the {@link ServiceEvent} that triggers unsubscription
     * @return a reusable {@link rx.Observable.Transformer} which unsubscribes when the event triggers.
     */
    <T> Observable.Transformer<? super T, ? extends T> bindUntilEvent(ServiceEvent event);

    /**
     * Binds a source until the next reasonable {@link ServiceEvent} occurs.
     * <p>
     * Intended for use with {@link Observable#compose(Observable.Transformer)}
     *
     * @return a reusable {@link rx.Observable.Transformer} which unsubscribes at the correct time.
     */
    <T> Observable.Transformer<? super T, ? extends T> bindToLifecycle();
}
