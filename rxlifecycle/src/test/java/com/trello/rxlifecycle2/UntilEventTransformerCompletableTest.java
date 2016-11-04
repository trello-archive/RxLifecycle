package com.trello.rxlifecycle2;

import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CancellationException;

public class UntilEventTransformerCompletableTest {

    PublishSubject<Object> subject;
    Completable completable;
    PublishSubject<String> lifecycle;

    @Before
    public void setup() {
        subject =  PublishSubject.create();
        completable = Completable.fromObservable(subject);
        lifecycle = PublishSubject.create();
    }

    @Test
    public void noEvents() {
        TestObserver<Void> testObserver = completable
            .compose(new UntilEventTransformer<String, String>(lifecycle, "stop"))
            .test();

        subject.onComplete();
        testObserver.assertComplete();
    }

    @Test
    public void oneWrongEvent() {
        TestObserver<Void> testObserver = completable
            .compose(new UntilEventTransformer<String, String>(lifecycle, "stop"))
            .test();

        lifecycle.onNext("keep going");
        subject.onComplete();
        testObserver.assertComplete();
    }

    @Test
    public void twoEvents() {
        TestObserver<Void> testObserver = completable
            .compose(new UntilEventTransformer<String, String>(lifecycle, "stop"))
            .test();

        lifecycle.onNext("keep going");
        lifecycle.onNext("stop");
        subject.onComplete();
        testObserver.assertError(CancellationException.class);
    }

}