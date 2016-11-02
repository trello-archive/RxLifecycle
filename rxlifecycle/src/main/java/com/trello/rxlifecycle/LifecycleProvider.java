package com.trello.rxlifecycle;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

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
    @Nonnull
    @CheckReturnValue
    Observable<E> lifecycle();

    /**
     * Binds a source until a specific event occurs.
     * <p>
     * Intended for use with {@link Observable#compose(ObservableTransformer)}
     *
     * @param event the event that triggers unsubscription
     * @return a reusable {@link ObservableTransformer} which unsubscribes when the event triggers.
     */
    @Nonnull
    @CheckReturnValue
    <T> LifecycleTransformer<T> bindUntilEvent(@Nonnull E event);

    /**
     * Binds a source until the next reasonable event occurs.
     * <p>
     * Intended for use with {@link Observable#compose(ObservableTransformer)}
     *
     * @return a reusable {@link ObservableTransformer} which unsubscribes at the correct time.
     */
    @Nonnull
    @CheckReturnValue
    <T> LifecycleTransformer<T> bindToLifecycle();
}
