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

package com.trello.rxlifecycle2;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import static com.trello.rxlifecycle2.internal.Preconditions.checkNotNull;

public class RxLifecycle {

    private RxLifecycle() {
        throw new AssertionError("No instances");
    }

    /**
     * Binds the given source to a lifecycle.
     * <p>
     * When the lifecycle event occurs, the source will cease to emit any notifications.
     *
     * @param lifecycle the lifecycle sequence
     * @param event the event which should conclude notifications from the source
     * @return a reusable {@link LifecycleTransformer} that unsubscribes the source at the specified event
     */
    @Nonnull
    @CheckReturnValue
    public static <T, R> LifecycleTransformer<T> bindUntilEvent(@Nonnull final Observable<R> lifecycle,
                                                                @Nonnull final R event) {
        checkNotNull(lifecycle, "lifecycle == null");
        checkNotNull(event, "event == null");
        return bind(takeUntilEvent(lifecycle, event));
    }

    private static <R> Observable<R> takeUntilEvent(final Observable<R> lifecycle, final R event) {
        return lifecycle.filter(new Predicate<R>() {
            @Override
            public boolean test(R lifecycleEvent) throws Exception {
                return lifecycleEvent.equals(event);
            }
        });
    }

    /**
     * Binds the given source to a lifecycle.
     * <p>
     * This helper automatically determines (based on the lifecycle sequence itself) when the source
     * should stop emitting items. Note that for this method, it assumes <em>any</em> event
     * emitted by the given lifecycle indicates that the lifecycle is over.
     *
     * @param lifecycle the lifecycle sequence
     * @return a reusable {@link LifecycleTransformer} that unsubscribes the source whenever the lifecycle emits
     */
    @Nonnull
    @CheckReturnValue
    public static <T, R> LifecycleTransformer<T> bind(@Nonnull final Observable<R> lifecycle) {
        return new LifecycleTransformer<>(lifecycle);
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
     * @return a reusable {@link LifecycleTransformer} that unsubscribes the source during the Fragment lifecycle
     */
    @Nonnull
    @CheckReturnValue
    public static <T, R> LifecycleTransformer<T> bind(@Nonnull Observable<R> lifecycle,
                                                      @Nonnull final Function<R, R> correspondingEvents) {
        checkNotNull(lifecycle, "lifecycle == null");
        checkNotNull(correspondingEvents, "correspondingEvents == null");
        return bind(takeUntilCorrespondingEvent(lifecycle.share(), correspondingEvents));
    }

    private static <R> Observable<Boolean> takeUntilCorrespondingEvent(final Observable<R> lifecycle,
                                                                       final Function<R, R> correspondingEvents) {
        return Observable.combineLatest(
            lifecycle.take(1).map(correspondingEvents),
            lifecycle.skip(1),
            new BiFunction<R, R, Boolean>() {
                @Override
                public Boolean apply(R bindUntilEvent, R lifecycleEvent) throws Exception {
                    return lifecycleEvent.equals(bindUntilEvent);
                }
            })
            .onErrorReturn(Functions.RESUME_FUNCTION)
            .filter(Functions.SHOULD_COMPLETE);
    }
}
