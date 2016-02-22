package com.trello.rxlifecycle.navi;

import android.support.annotation.NonNull;
import com.trello.navi.Event;
import com.trello.navi.NaviComponent;
import com.trello.navi.rx.RxNavi;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.ActivityLifecycleProvider;
import com.trello.rxlifecycle.RxLifecycle;
import rx.Observable;
import rx.subjects.BehaviorSubject;

final class ActivityLifecycleProviderImpl implements ActivityLifecycleProvider {
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
    public Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    @Override
    @NonNull
    public <T> Observable.Transformer<T, T> bindUntilEvent(@NonNull ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    public <T> Observable.Transformer<T, T> bindToLifecycle() {
        return RxLifecycle.bindActivity(lifecycleSubject);
    }
}
