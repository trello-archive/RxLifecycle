package com.trello.rxlifecycle.events.support;

import android.content.Intent;
import com.trello.rxlifecycle.components.support.RxDialogFragment;
import com.trello.rxlifecycle.events.ActivityResultEvent;
import com.trello.rxlifecycle.events.FragmentLifecycleEventProvider;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class RxEventDialogFragment extends RxDialogFragment implements FragmentLifecycleEventProvider {

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
