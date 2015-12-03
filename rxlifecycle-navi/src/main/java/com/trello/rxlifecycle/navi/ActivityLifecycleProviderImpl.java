package com.trello.rxlifecycle.navi;

import android.os.Bundle;
import com.trello.navi.Event;
import com.trello.navi.Listener;
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

        activity.addListener(Event.CREATE, new Listener<Bundle>() {
            @Override
            public void call(Bundle bundle) {
                RxNavi.observe(activity, Event.ALL)
                    .map(NaviLifecycleMaps.ACTIVITY_EVENT_MAP)
                    .filter(RxUtils.notNull())
                    .takeUntil(RxNavi.observe(activity, Event.DESTROY))
                    .subscribe(lifecycleSubject);

                // Subscribing in onCreate won't emit, so we need to do this ourselves
                lifecycleSubject.onNext(ActivityEvent.CREATE);
            }
        });
    }

    @Override
    public Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    @Override
    public <T> Observable.Transformer<T, T> bindUntilEvent(ActivityEvent event) {
        return RxLifecycle.bindUntilActivityEvent(lifecycleSubject, event);
    }

    @Override
    public <T> Observable.Transformer<T, T> bindToLifecycle() {
        return RxLifecycle.bindActivity(lifecycleSubject);
    }
}
