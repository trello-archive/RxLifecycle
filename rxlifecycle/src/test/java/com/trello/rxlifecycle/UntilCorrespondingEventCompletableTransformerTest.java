package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;
import rx.Completable;
import rx.functions.Func1;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import java.util.concurrent.CancellationException;

public class UntilCorrespondingEventCompletableTransformerTest {

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
    public void noEvents() {
        completable
            .compose(new UntilCorrespondingEventCompletableTransformer<>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        subject.onCompleted();
        testSubscriber.assertCompleted();
    }

    @Test
    public void oneStartEvent() {
        completable
            .compose(new UntilCorrespondingEventCompletableTransformer<>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("create");
        subject.onCompleted();
        testSubscriber.assertCompleted();
    }

    @Test
    public void twoOpenEvents() {
        completable
            .compose(new UntilCorrespondingEventCompletableTransformer<>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("create");
        lifecycle.onNext("start");
        subject.onCompleted();
        testSubscriber.assertCompleted();
    }

    @Test
    public void openAndCloseEvent() {
        completable
            .compose(new UntilCorrespondingEventCompletableTransformer<>(lifecycle, CORRESPONDING_EVENTS))
            .subscribe(testSubscriber);

        lifecycle.onNext("create");
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