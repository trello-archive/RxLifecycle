package com.trello.rxlifecycle;

import org.jetbrains.annotations.NotNull;
import rx.Observable;

/**
 * Common base interface for activity and fragment lifecycle providers.
 *
 * Useful if you are writing utilities on top of rxlifecycle-components
 * or implementing your own component not supported by this library.
 */
public interface LifecycleProvider<E> {
    /**
     * @return a sequence of lifecycle events
     */
    @NotNull
    Observable<E> lifecycle();

    /**
     * Binds a source until a specific event occurs.
     * <p>
     * Intended for use with {@link Observable#compose(Observable.Transformer)}
     *
     * @param event the event that triggers unsubscription
     * @return a reusable {@link Observable.Transformer} which unsubscribes when the event triggers.
     */
    @NotNull
    <T> LifecycleTransformer<T> bindUntilEvent(@NotNull E event);

    /**
     * Binds a source until the next reasonable event occurs.
     * <p>
     * Intended for use with {@link Observable#compose(Observable.Transformer)}
     *
     * @return a reusable {@link Observable.Transformer} which unsubscribes at the correct time.
     */
    @NotNull
    <T> LifecycleTransformer<T> bindToLifecycle();
}
