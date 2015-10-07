package com.trello.rxlifecycle.events.support;

import android.content.Intent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle.events.ActivityLifecycleEventProvider;
import com.trello.rxlifecycle.events.ActivityResultEvent;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class RxEventAppCompatActivity extends RxAppCompatActivity implements ActivityLifecycleEventProvider {

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
