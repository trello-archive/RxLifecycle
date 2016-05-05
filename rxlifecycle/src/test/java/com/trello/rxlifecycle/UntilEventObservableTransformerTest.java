package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

public class UntilEventObservableTransformerTest {

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
    public void noEvents() {
        observable
            .compose(new UntilEventObservableTransformer<String, String>(lifecycle, "stop"))
            .subscribe(testSubscriber);

        observable.onNext("1");
        observable.onNext("2");

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNoTerminalEvent();
    }

    @Test
    public void oneWrongEvent() {
        observable
            .compose(new UntilEventObservableTransformer<String, String>(lifecycle, "stop"))
            .subscribe(testSubscriber);

        observable.onNext("1");
        lifecycle.onNext("keep going");
        observable.onNext("2");

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNoTerminalEvent();
    }

    @Test
    public void twoEvents() {
        observable
            .compose(new UntilEventObservableTransformer<String, String>(lifecycle, "stop"))
            .subscribe(testSubscriber);

        observable.onNext("1");
        lifecycle.onNext("keep going");
        observable.onNext("2");
        lifecycle.onNext("stop");
        observable.onNext("3");

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertCompleted();
    }

}