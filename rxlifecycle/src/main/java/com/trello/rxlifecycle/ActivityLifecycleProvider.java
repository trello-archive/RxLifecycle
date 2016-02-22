package com.trello.rxlifecycle;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import rx.Observable;

/**
 * Common interface for all RxActivity extensions.
 *
 * Useful if you are writing utilities on top of rxlifecycle-components,
 * or you are implementing your own component not supported in this library.
 */
public interface ActivityLifecycleProvider {

    /**
     * @return a sequence of {@link android.app.Activity} lifecycle events
     */
    @NonNull
    @CheckResult
    Observable<ActivityEvent> lifecycle();

    /**
     * Binds a source until a specific {@link ActivityEvent} occurs.
     * <p>
     * Intended for use with {@link Observable#compose(Observable.Transformer)}
     *
     * @param event the {@link ActivityEvent} that triggers unsubscription
     * @return a reusable {@link rx.Observable.Transformer} which unsubscribes when the event triggers.
     */
    @NonNull
    @CheckResult
    <T> Observable.Transformer<T, T> bindUntilEvent(@NonNull ActivityEvent event);

    /**
     * Binds a source until the next reasonable {@link ActivityEvent} occurs.
     * <p>
     * Intended for use with {@link Observable#compose(Observable.Transformer)}
     *
     * @return a reusable {@link rx.Observable.Transformer} which unsubscribes at the correct time.
     */
    @NonNull
    @CheckResult
    <T> Observable.Transformer<T, T> bindToLifecycle();

}
