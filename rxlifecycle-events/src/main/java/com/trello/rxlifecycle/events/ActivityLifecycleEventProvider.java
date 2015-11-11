package com.trello.rxlifecycle.events;

import rx.Observable;

public interface ActivityLifecycleEventProvider {

    /**
     * @return a sequence of {@link com.trello.rxlifecycle.events.ActivityResultEvent} events
     */
    Observable<ActivityResultEvent> activityResult();

}
