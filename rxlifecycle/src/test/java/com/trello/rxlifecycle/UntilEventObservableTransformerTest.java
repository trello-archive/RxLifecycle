package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

public class UntilEventObservableTransformerTest {

    PublishSubject<String> lifecycle;
    TestSubscriber<String> testSubscriber;

    @Before
    public void setup() {
        lifecycle = PublishSubject.create();
        testSubscriber = new TestSubscriber<>(0);
    }

    @Test
    public void noEvents() {
        Observable.just("1", "2", "3")
            .compose(new UntilEventObservableTransformer<String, String>(lifecycle, "stop"))
            .subscribe(testSubscriber);

        testSubscriber.requestMore(2);
        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNoTerminalEvent();
    }

    @Test
    public void oneWrongEvent() {
        Observable.just("1", "2", "3")
            .compose(new UntilEventObservableTransformer<String, String>(lifecycle, "stop"))
            .subscribe(testSubscriber);

        testSubscriber.requestMore(1);
        lifecycle.onNext("keep going");
        testSubscriber.requestMore(1);

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNoTerminalEvent();
    }

    @Test
    public void twoEvents() {
        Observable.just("1", "2", "3")
            .compose(new UntilEventObservableTransformer<String, String>(lifecycle, "stop"))
            .subscribe(testSubscriber);

        testSubscriber.requestMore(1);
        lifecycle.onNext("keep going");
        testSubscriber.requestMore(1);
        lifecycle.onNext("stop");
        testSubscriber.requestMore(1);

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertCompleted();
    }

}