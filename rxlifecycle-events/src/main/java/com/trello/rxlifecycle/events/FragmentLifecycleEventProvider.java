package com.trello.rxlifecycle.events;

import rx.Observable;

public interface FragmentLifecycleEventProvider {

    Observable<ActivityResultEvent> activityResult();

}