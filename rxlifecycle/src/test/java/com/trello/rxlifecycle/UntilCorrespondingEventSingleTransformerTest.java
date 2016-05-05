package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;
import rx.Single;
import rx.functions.Func1;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import java.util.concurrent.CancellationException;

public class UntilCorrespondingEventSingleTransformerTest {

    PublishSubject<String> lifecycle;
    TestSubscriber<String> testSubscriber;

    @Before
    public void setup() {
        lifecycle = PublishSubject.create();
        testSubscriber = new TestSubscriber<>(0);
    }

    @Test
    public void noEvents() {
        Single.just("1")
            .compose(new UntilCorrespondingEventSingleTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        testSubscriber.requestMore(1);
        testSubscriber.assertValue("1");
        testSubscriber.assertCompleted();
    }

    @Test
    public void oneStartEvent() {
        Single.just("1")
            .compose(new UntilCorrespondingEventSingleTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("create");
        testSubscriber.requestMore(1);
        testSubscriber.assertValue("1");
        testSubscriber.assertCompleted();
    }

    @Test
    public void twoOpenEvents() {
        Single.just("1")
            .compose(new UntilCorrespondingEventSingleTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("create");
        lifecycle.onNext("start");
        testSubscriber.requestMore(1);
        testSubscriber.assertValue("1");
        testSubscriber.assertCompleted();
    }

    @Test
    public void openAndCloseEvent() {
        Single.just("1")
            .compose(new UntilCorrespondingEventSingleTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("create");
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