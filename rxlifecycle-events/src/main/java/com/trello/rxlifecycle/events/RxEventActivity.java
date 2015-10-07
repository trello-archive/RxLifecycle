package com.trello.rxlifecycle.events;

import android.content.Intent;
import com.trello.rxlifecycle.components.RxActivity;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class RxEventActivity extends RxActivity implements ActivityLifecycleEventProvider {

    private final BehaviorSubject<ActivityResultEvent> activityResultSubject = BehaviorSubject.create();

    @Override
    public Observable<ActivityResultEvent> activityResult() {
        return activityResultSubject.asObservable();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        activityResultSubject.onNext(ActivityResultEvent.create(requestCode, resultCode, data));
    }
}
