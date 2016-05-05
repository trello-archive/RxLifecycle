package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

public class UntilLifecycleObservableTransformerTest {

    PublishSubject<String> lifecycle;
    TestSubscriber<String> testSubscriber;

    @Before
    public void setup() {
        lifecycle = PublishSubject.create();
        testSubscriber = new TestSubscriber<>(0);
    }

    @Test
    public void noEvent() {
        Observable.just("1", "2", "3")
            .compose(new UntilLifecycleObservableTransformer<String, String>(lifecycle))
            .subscribe(testSubscriber);

        testSubscriber.requestMore(2);
        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNoTerminalEvent();
    }

    @Test
    public void oneEvent() {
        Observable.just("1", "2", "3")
            .compose(new UntilLifecycleObservableTransformer<String, String>(lifecycle))
            .subscribe(testSubscriber);

        testSubscriber.requestMore(1);
        lifecycle.onNext("stop");
        testSubscriber.requestMore(1);

        testSubscriber.assertValues("1");
        testSubscriber.assertCompleted();
    }
}