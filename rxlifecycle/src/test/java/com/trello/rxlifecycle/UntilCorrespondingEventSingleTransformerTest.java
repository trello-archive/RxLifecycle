package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CancellationException;

import rx.Single;
import rx.functions.Func1;
import rx.observers.TestSubscriber;
import rx.subjects.BehaviorSubject;

public class UntilCorrespondingEventSingleTransformerTest {

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
        Single.just("1")
            .compose(new UntilCorrespondingEventSingleTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        testSubscriber.requestMore(1);
        testSubscriber.assertValue("1");
        testSubscriber.assertCompleted();
    }

    @Test
    public void oneStartEvent() {
        lifecycle.onNext("create");
        Single.just("1")
            .compose(new UntilCorrespondingEventSingleTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("resume");
        testSubscriber.requestMore(1);
        testSubscriber.assertValue("1");
        testSubscriber.assertCompleted();
    }

    @Test
    public void twoOpenEvents() {
        lifecycle.onNext("create");
        Single.just("1")
            .compose(new UntilCorrespondingEventSingleTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("start");
        lifecycle.onNext("resume");
        testSubscriber.requestMore(1);
        testSubscriber.assertValue("1");
        testSubscriber.assertCompleted();
    }

    @Test
    public void openAndCloseEvent() {
        lifecycle.onNext("create");
        Single.just("1")
            .compose(new UntilCorrespondingEventSingleTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("start");
        lifecycle.onNext("destroy");
        testSubscriber.requestMore(1);
        testSubscriber.assertNoValues();
        testSubscriber.assertError(CancellationException.class);
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
