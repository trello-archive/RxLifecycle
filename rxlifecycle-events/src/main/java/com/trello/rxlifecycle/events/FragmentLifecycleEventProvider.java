package com.trello.rxlifecycle.events;

import rx.Observable;

public interface FragmentLifecycleEventProvider {

    /**
     * @return a sequence of {@link com.trello.rxlifecycle.events.ActivityResultEvent} events
     */
    Observable<ActivityResultEvent> activityResult();

}