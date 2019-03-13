package com.trello.lifecycle2.android.lifecycle;

import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.RxLifecycle;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Wraps a {@link LifecycleOwner} so that it can be used as a {@link LifecycleProvider}. For example,
 * you can do
 * <pre>{@code
 * LifecycleProvider<Lifecycle.Event> provider = AndroidLifecycle.createLifecycleProvider(this);
 * myObservable
 *     .compose(provider.bindLifecycle())
 *     .subscribe();
 * }</pre>
 * where {@code this} is a {@code android.arch.lifecycle.LifecycleActivity} or
 * {@code android.arch.lifecycle.LifecycleFragment}.
 */
public final class AndroidLifecycle implements LifecycleProvider<Lifecycle.Event>, LifecycleObserver {

    public static LifecycleProvider<Lifecycle.Event> createLifecycleProvider(LifecycleOwner owner) {
        return new AndroidLifecycle(owner);
    }

    private final BehaviorSubject<Lifecycle.Event> lifecycleSubject = BehaviorSubject.create();

    private AndroidLifecycle(LifecycleOwner owner) {
        owner.getLifecycle().addObserver(this);
    }

    @NonNull
    @Override
    @CheckResult
    public Observable<Lifecycle.Event> lifecycle() {
        return lifecycleSubject.hide();
    }

    @NonNull
    @Override
    @CheckResult
    public <T> LifecycleTransformer<T> bindUntilEvent(@NonNull Lifecycle.Event... events) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, events);
    }

    @NonNull
    @Override
    @CheckResult
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroidLifecycle.bindLifecycle(lifecycleSubject);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onEvent(LifecycleOwner owner, Lifecycle.Event event) {
        lifecycleSubject.onNext(event);
        if (event == Lifecycle.Event.ON_DESTROY) {
            owner.getLifecycle().removeObserver(this);
        }
    }
}
