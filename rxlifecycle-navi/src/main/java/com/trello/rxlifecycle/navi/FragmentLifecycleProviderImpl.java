package com.trello.rxlifecycle.navi;

import android.support.annotation.NonNull;
import com.trello.navi.Event;
import com.trello.navi.NaviComponent;
import com.trello.navi.rx.RxNavi;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.FragmentLifecycleProvider;
import com.trello.rxlifecycle.RxLifecycle;
import rx.Observable;
import rx.subjects.BehaviorSubject;

final class FragmentLifecycleProviderImpl implements FragmentLifecycleProvider {
    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    public FragmentLifecycleProviderImpl(final NaviComponent fragment) {
        if (!fragment.handlesEvents(Event.ATTACH, Event.CREATE, Event.CREATE_VIEW, Event.START, Event.RESUME,
            Event.PAUSE, Event.STOP, Event.DESTROY_VIEW, Event.DESTROY, Event.DETACH)) {
            throw new IllegalArgumentException("NaviComponent does not handle all required events");
        }

        RxNavi.observe(fragment, Event.ALL)
            .map(NaviLifecycleMaps.FRAGMENT_EVENT_MAP)
            .filter(RxUtils.notNull())
            .subscribe(lifecycleSubject);
    }

    @Override
    @NonNull
    public Observable<FragmentEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    @Override
    @NonNull
    public <T> Observable.Transformer<T, T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    public <T> Observable.Transformer<T, T> bindToLifecycle() {
        return RxLifecycle.bindFragment(lifecycleSubject);
    }
}
