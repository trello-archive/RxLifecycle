package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.functions.Func1;
import rx.observers.TestSubscriber;
import rx.subjects.BehaviorSubject;

public class UntilCorrespondingEventObservableTransformerTest {

    BehaviorSubject<String> lifecycle;
    TestSubscriber<String> testSubscriber;

    @Before
    public void setup() {
        lifecycle = BehaviorSubject.create();
        testSubscriber = new TestSubscriber<>(0);
    }

    @Test
    public void noEvents() {
        lifecycle.onNext("create");
        Observable.just("1", "2", "3")
            .compose(new UntilCorrespondingEventObservableTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        testSubscriber.requestMore(2);
        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNoTerminalEvent();
    }

    @Test
    public void oneStartEvent() {
        lifecycle.onNext("create");
        Observable.just("1", "2", "3")
            .compose(new UntilCorrespondingEventObservableTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("start");
        testSubscriber.requestMore(2);

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNoTerminalEvent();
    }

    @Test
    public void twoOpenEvents() {
        lifecycle.onNext("create");
        Observable.just("1", "2", "3")
            .compose(new UntilCorrespondingEventObservableTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("start");
        testSubscriber.requestMore(1);
        lifecycle.onNext("resume");
        testSubscriber.requestMore(1);

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNoTerminalEvent();
    }

    @Test
    public void openAndCloseEvent() {
        lifecycle.onNext("create");
        Observable.just("1", "2", "3")
            .compose(new UntilCorrespondingEventObservableTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("start");
        testSubscriber.requestMore(1);
        lifecycle.onNext("destroy");
        testSubscriber.requestMore(1);

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
