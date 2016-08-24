/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trello.rxlifecycle;

import rx.Observable;
import rx.functions.Func1;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import static com.trello.rxlifecycle.internal.Preconditions.checkNotNull;

public class RxLifecycle {

    private RxLifecycle() {
        throw new AssertionError("No instances");
    }

    /**
     * Binds the given source to a lifecycle.
     * <p>
     * When the lifecycle event occurs, the source will cease to emit any notifications.
     * <p>
     * Use with {@link Observable#compose(Observable.Transformer)}:
     * {@code source.compose(RxLifecycle.bindUntilEvent(lifecycle, ActivityEvent.STOP)).subscribe()}
     *
     * @param lifecycle the lifecycle sequence
     * @param event the event which should conclude notifications from the source
     * @return a reusable {@link Observable.Transformer} that unsubscribes the source at the specified event
     */
    @Nonnull
    @CheckReturnValue
    public static <T, R> LifecycleTransformer<T> bindUntilEvent(@Nonnull final Observable<R> lifecycle,
                                                                @Nonnull final R event) {
        checkNotNull(lifecycle, "lifecycle == null");
        checkNotNull(event, "event == null");

        return new UntilEventObservableTransformer<>(lifecycle, event);
    }

    /**
     * Binds the given source to a lifecycle.
     * <p>
     * Use with {@link Observable#compose(Observable.Transformer)}:
     * {@code source.compose(RxLifecycle.bind(lifecycle)).subscribe()}
     * <p>
     * This helper automatically determines (based on the lifecycle sequence itself) when the source
     * should stop emitting items. Note that for this method, it assumes <em>any</em> event
     * emitted by the given lifecycle indicates that the lifecycle is over.
     *
     * @param lifecycle the lifecycle sequence
     * @return a reusable {@link Observable.Transformer} that unsubscribes the source whenever the lifecycle emits
     */
    @Nonnull
    @CheckReturnValue
    public static <T, R> LifecycleTransformer<T> bind(@Nonnull final Observable<R> lifecycle) {
        checkNotNull(lifecycle, "lifecycle == null");

        return new UntilLifecycleObservableTransformer<>(lifecycle);
    }

    /**
     * Binds the given source to a lifecycle.
     * <p>
     * This method determines (based on the lifecycle sequence itself) when the source
     * should stop emitting items. It uses the provided correspondingEvents function to determine
     * when to unsubscribe.
     * <p>
     * Note that this is an advanced usage of the library and should generally be used only if you
     * really know what you're doing with a given lifecycle.
     *
     * @param lifecycle the lifecycle sequence
     * @param correspondingEvents a function which tells the source when to unsubscribe
     * @return a reusable {@link Observable.Transformer} that unsubscribes the source during the Fragment lifecycle
     */
    @Nonnull
    @CheckReturnValue
    public static <T, R> LifecycleTransformer<T> bind(@Nonnull Observable<R> lifecycle,
                                                      @Nonnull final Func1<R, R> correspondingEvents) {
        checkNotNull(lifecycle, "lifecycle == null");
        checkNotNull(correspondingEvents, "correspondingEvents == null");

        // Keep emitting from source until the corresponding event occurs in the lifecycle
        return new UntilCorrespondingEventObservableTransformer<>(lifecycle.share(), correspondingEvents);
    }
}
