package com.trello.rxlifecycle.components;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.ServiceEvent;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class RxService extends Service implements ServiceLifecycleProvider {

    private final BehaviorSubject<ServiceEvent> lifecycleSubject = BehaviorSubject.create();

    @Override
    public final Observable<ServiceEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    @Override
    public final <T> Observable.Transformer<? super T, ? extends T> bindUntilEvent(ServiceEvent event) {
        return RxLifecycle.bindUntilServiceEvent(lifecycleSubject, event);
    }

    @Override
    public final <T> Observable.Transformer<? super T, ? extends T> bindToLifecycle() {
        return RxLifecycle.bindService(lifecycleSubject);
    }

    @Override
    @CallSuper
    public void onCreate() {
        super.onCreate();
        lifecycleSubject.onNext(ServiceEvent.CREATE);
    }

    @Nullable
    @Override
    @CallSuper
    public IBinder onBind(Intent intent) {
        lifecycleSubject.onNext(ServiceEvent.BIND);
        return null;
    }

    @Override
    @CallSuper
    public int onStartCommand(Intent intent, int flags, int startId) {
        lifecycleSubject.onNext(ServiceEvent.START_COMMAND);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    @CallSuper
    public void onDestroy() {
        lifecycleSubject.onNext(ServiceEvent.DESTROY);
        super.onDestroy();
    }

    @Override
    @CallSuper
    public boolean onUnbind(Intent intent) {
        lifecycleSubject.onNext(ServiceEvent.UNBIND);
        return super.onUnbind(intent);
    }

    @Override
    @CallSuper
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        lifecycleSubject.onNext(ServiceEvent.REBIND);
    }
}
