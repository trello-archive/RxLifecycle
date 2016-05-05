package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

public class UntilLifecycleObservableTransformerTest {

    PublishSubject<String> observable;
    PublishSubject<String> lifecycle;
    TestSubscriber<String> testSubscriber;

    @Before
    public void setup() {
        observable = PublishSubject.create();
        lifecycle = PublishSubject.create();
        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void noEvent() {
        observable
            .compose(new UntilLifecycleObservableTransformer<String, String>(lifecycle))
            .subscribe(testSubscriber);

        observable.onNext("1");
        observable.onNext("2");

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNoTerminalEvent();
    }

    @Test
    public void oneEvent() {
        observable
            .compose(new UntilLifecycleObservableTransformer<String, String>(lifecycle))
            .subscribe(testSubscriber);

        observable.onNext("1");
        lifecycle.onNext("stop");
        observable.onNext("2");

        testSubscriber.assertValues("1");
        testSubscriber.assertCompleted();
    }
}