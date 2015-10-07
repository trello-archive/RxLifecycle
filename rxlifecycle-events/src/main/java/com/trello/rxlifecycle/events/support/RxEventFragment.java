package com.trello.rxlifecycle.events.support;

import android.content.Intent;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.trello.rxlifecycle.events.ActivityResultEvent;
import com.trello.rxlifecycle.events.FragmentLifecycleEventProvider;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class RxEventFragment extends RxFragment implements FragmentLifecycleEventProvider {

    private final BehaviorSubject<ActivityResultEvent> activityResultSubject = BehaviorSubject.create();

    @Override
    public Observable<ActivityResultEvent> activityResult() {
        return activityResultSubject.asObservable();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        activityResultSubject.onNext(ActivityResultEvent.create(requestCode, resultCode, data));
    }
}
