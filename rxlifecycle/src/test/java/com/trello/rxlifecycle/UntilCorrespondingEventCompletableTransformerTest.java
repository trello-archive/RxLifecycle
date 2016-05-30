package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CancellationException;

import rx.Completable;
import rx.functions.Func1;
import rx.observers.TestSubscriber;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class UntilCorrespondingEventCompletableTransformerTest {

    PublishSubject<Object> subject;
    Completable completable;
    BehaviorSubject<String> lifecycle;
    TestSubscriber<String> testSubscriber;

    @Before
    public void setup() {
        subject =  PublishSubject.create();
        completable = Completable.fromObservable(subject);
        lifecycle = BehaviorSubject.create();
        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void noEvents() {
        lifecycle.onNext("create");
        completable
            .compose(new UntilCorrespondingEventCompletableTransformer<>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        subject.onCompleted();
        testSubscriber.assertCompleted();
    }

    @Test
    public void oneStartEvent() {
        lifecycle.onNext("create");
        completable
            .compose(new UntilCorrespondingEventCompletableTransformer<>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("start");
        subject.onCompleted();
        testSubscriber.assertCompleted();
    }

    @Test
    public void twoOpenEvents() {
        lifecycle.onNext("create");
        completable
            .compose(new UntilCorrespondingEventCompletableTransformer<>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("start");
        lifecycle.onNext("resume");
        subject.onCompleted();
        testSubscriber.assertCompleted();
    }

    @Test
    public void openAndCloseEvent() {
        lifecycle.onNext("create");
        completable
            .compose(new UntilCorrespondingEventCompletableTransformer<>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("start");
        lifecycle.onNext("destroy");
        subject.onCompleted();
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
