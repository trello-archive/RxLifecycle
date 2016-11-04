package com.trello.rxlifecycle.navi;

import com.trello.navi2.Event;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;
import io.reactivex.functions.Function;

/**
 * Maps from Navi events to RxLifecycleAndroid events
 */
final class NaviLifecycleMaps {

    static final Function<Event.Type, ActivityEvent> ACTIVITY_EVENT_MAP =
        new Function<Event.Type, ActivityEvent>() {
            @Override
            public ActivityEvent apply(Event.Type type) throws Exception {
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

    static final Function<Event.Type, FragmentEvent> FRAGMENT_EVENT_MAP =
        new Function<Event.Type, FragmentEvent>() {
            @Override
            public FragmentEvent apply(Event.Type type) throws Exception {
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
