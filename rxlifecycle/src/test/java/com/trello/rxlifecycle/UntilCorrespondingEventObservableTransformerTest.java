package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;
import rx.functions.Func1;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

public class UntilCorrespondingEventObservableTransformerTest {

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
            .compose(new UntilCorrespondingEventObservableTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        observable.onNext("1");
        observable.onNext("2");

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNoTerminalEvent();
    }

    @Test
    public void oneStartEvent() {
        observable
            .compose(new UntilCorrespondingEventObservableTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("create");
        observable.onNext("1");
        observable.onNext("2");

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNoTerminalEvent();
    }

    @Test
    public void twoOpenEvents() {
        observable
            .compose(new UntilCorrespondingEventObservableTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("create");
        observable.onNext("1");
        lifecycle.onNext("start");
        observable.onNext("2");

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNoTerminalEvent();
    }

    @Test
    public void openAndCloseEvent() {
        observable
            .compose(new UntilCorrespondingEventObservableTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("create");
        observable.onNext("1");
        lifecycle.onNext("destroy");
        observable.onNext("2");

        testSubscriber.assertValues("1");
        testSubscriber.assertCompleted();
    }

    private static final Func1<String, String> CORRESPONDING_EVENTS = new Func1<String, String>() {
        @Override
        public String call(String s) {
            if (s.equals("create")) {
                return "destroy";
            }

            throw new IllegalArgumentException("Cannot handle: " + s);
        }
    };
}