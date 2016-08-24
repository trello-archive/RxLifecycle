package com.trello.rxlifecycle.navi;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.trello.navi.Event;
import com.trello.navi.NaviComponent;
import com.trello.navi.rx.RxNavi;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import rx.Observable;
import rx.subjects.BehaviorSubject;

final class ActivityLifecycleProviderImpl implements LifecycleProvider<ActivityEvent> {
    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();

    public ActivityLifecycleProviderImpl(final NaviComponent activity) {
        if (!activity.handlesEvents(Event.CREATE, Event.START, Event.RESUME, Event.PAUSE, Event.STOP, Event.DESTROY)) {
            throw new IllegalArgumentException("NaviComponent does not handle all required events");
        }

        RxNavi.observe(activity, Event.ALL)
            .map(NaviLifecycleMaps.ACTIVITY_EVENT_MAP)
            .filter(RxUtils.notNull())
            .subscribe(lifecycleSubject);
    }

    @Override
    @NonNull
    @CheckResult
    public Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    @Override
    @NonNull
    @CheckResult
    public <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(lifecycleSubject);
    }
}
