package com.trello.rxlifecycle.navi;

import com.trello.navi.Event;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;
import rx.functions.Func1;

/**
 * Maps from Navi events to RxLifecycleAndroid events
 */
final class NaviLifecycleMaps {

    static final Func1<Event.Type, ActivityEvent> ACTIVITY_EVENT_MAP =
        new Func1<Event.Type, ActivityEvent>() {
            @Override
            public ActivityEvent call(Event.Type type) {
                switch (type) {
                    case CREATE:
                        return ActivityEvent.CREATE;
                    case START:
                        return ActivityEvent.START;
                    case RESUME:
                        return ActivityEvent.RESUME;
                    case PAUSE:
                        return ActivityEvent.PAUSE;
                    case STOP:
                        return ActivityEvent.STOP;
                    case DESTROY:
                        return ActivityEvent.DESTROY;
                    default:
                        return null;
                }
            }
        };

    static final Func1<Event.Type, FragmentEvent> FRAGMENT_EVENT_MAP =
        new Func1<Event.Type, FragmentEvent>() {
            @Override
            public FragmentEvent call(Event.Type type) {
                switch (type) {
                    case ATTACH:
                        return FragmentEvent.ATTACH;
                    case CREATE:
                        return FragmentEvent.CREATE;
                    case CREATE_VIEW:
                        return FragmentEvent.CREATE_VIEW;
                    case START:
                        return FragmentEvent.START;
                    case RESUME:
                        return FragmentEvent.RESUME;
                    case PAUSE:
                        return FragmentEvent.PAUSE;
                    case STOP:
                        return FragmentEvent.STOP;
                    case DESTROY_VIEW:
                        return FragmentEvent.DESTROY_VIEW;
                    case DESTROY:
                        return FragmentEvent.DESTROY;
                    case DETACH:
                        return FragmentEvent.DETACH;
                    default:
                        return null;
                }
            }
        };

    private NaviLifecycleMaps() {
        throw new AssertionError("No instances!");
    }
}
