package com.trello.rxlifecycle.events;

import android.content.Intent;
import com.trello.rxlifecycle.components.RxDialogFragment;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class RxEventDialogFragment extends RxDialogFragment implements FragmentLifecycleEventProvider {

    private final BehaviorSubject<com.trello.rxlifecycle.events.ActivityResultEvent> activityResultSubject = BehaviorSubject.create();

    @Override
    public Observable<ActivityResultEvent> activityResult() {
        return activityResultSubject.asObservable();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        activityResultSubject.onNext(com.trello.rxlifecycle.events.ActivityResultEvent.create(requestCode, resultCode, data));
    }
}
