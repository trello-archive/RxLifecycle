package com.trello.rxlifecycle.events;

import rx.Observable;

public interface ActivityLifecycleEventProvider {

    Observable<ActivityResultEvent> activityResult();

}
