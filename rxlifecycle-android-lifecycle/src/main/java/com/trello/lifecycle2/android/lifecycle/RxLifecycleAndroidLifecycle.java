package com.trello.lifecycle2.android.lifecycle;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.OutsideLifecycleException;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

import static com.trello.rxlifecycle2.RxLifecycle.bind;

public final class RxLifecycleAndroidLifecycle {

    private RxLifecycleAndroidLifecycle() {
        throw new AssertionError("No instances");
    }

    /**
     * Binds the given source to an Android lifecycle.
     * <p>
     * This helper automatically determines (based on the lifecycle sequence itself) when the source
     * should stop emitting items. In the case that the lifecycle sequence is in the
     * creation phase (ON_CREATE, ON_START, etc) it will choose the equivalent destructive phase (ON_DESTROY,
     * ON_STOP, etc). If used in the destructive phase, the notifications will cease at the next event;
     * for example, if used in ON_PAUSE, it will unsubscribe in ON_STOP.
     *
     * @param lifecycle the lifecycle sequence of an Activity
     * @return a reusable {@link LifecycleTransformer} that unsubscribes the source during the Activity lifecycle
     */
    @NonNull
    @CheckResult
    public static <T> LifecycleTransformer<T> bindLifecycle(@NonNull Observable<Lifecycle.Event> lifecycle) {
        return bind(lifecycle, LIFECYCLE);
    }

    private static final Function<Lifecycle.Event, Lifecycle.Event> LIFECYCLE = new Function<Lifecycle.Event, Lifecycle.Event>() {
        @Override
        public Lifecycle.Event apply(Lifecycle.Event lastEvent) throws Exception {
            switch (lastEvent) {
                case ON_CREATE:
                    return Lifecycle.Event.ON_DESTROY;
                case ON_START:
                    return Lifecycle.Event.ON_STOP;
                case ON_RESUME:
                    return Lifecycle.Event.ON_PAUSE;
                case ON_PAUSE:
                    return Lifecycle.Event.ON_STOP;
                case ON_STOP:
                    return Lifecycle.Event.ON_DESTROY;
                case ON_DESTROY:
                    throw new OutsideLifecycleException("Cannot bind to Activity lifecycle when outside of it.");
                default:
                    throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
            }
        }
    };
}
