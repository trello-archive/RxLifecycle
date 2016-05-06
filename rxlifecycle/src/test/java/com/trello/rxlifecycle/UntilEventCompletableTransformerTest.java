package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;
import rx.Completable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import java.util.concurrent.CancellationException;

public class UntilEventCompletableTransformerTest {

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
            .compose(new UntilEventCompletableTransformer<>(lifecycle, "stop"))
            .subscribe(testSubscriber);

        subject.onCompleted();
        testSubscriber.assertCompleted();
    }

    @Test
    public void oneWrongEvent() {
        completable
            .compose(new UntilEventCompletableTransformer<>(lifecycle, "stop"))
            .subscribe(testSubscriber);

        lifecycle.onNext("keep going");
        subject.onCompleted();
        testSubscriber.assertCompleted();
    }

    @Test
    public void twoEvents() {
        completable
            .compose(new UntilEventCompletableTransformer<>(lifecycle, "stop"))
            .subscribe(testSubscriber);

        lifecycle.onNext("keep going");
        lifecycle.onNext("stop");
        subject.onCompleted();
        testSubscriber.assertError(CancellationException.class);
    }

}