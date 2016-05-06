package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;
import rx.Completable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import java.util.concurrent.CancellationException;

public class UntilLifecycleCompletableTransformerTest {

    PublishSubject<Object> subject;
    Completable completable;
    PublishSubject<String> lifecycle;
    TestSubscriber<String> testSubscriber;

    @Before
    public void setup() {
        subject =  PublishSubject.create();
        completable = Completable.fromObservable(subject);
        lifecycle = PublishSubject.create();
        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void noEvent() {
        completable
            .compose(new UntilLifecycleCompletableTransformer<>(lifecycle))
            .subscribe(testSubscriber);

        subject.onCompleted();

        testSubscriber.assertCompleted();
    }

    @Test
    public void oneEvent() {
        completable
            .compose(new UntilLifecycleCompletableTransformer<>(lifecycle))
            .subscribe(testSubscriber);

        lifecycle.onNext("stop");
        subject.onCompleted();

        testSubscriber.assertError(CancellationException.class);
    }
}