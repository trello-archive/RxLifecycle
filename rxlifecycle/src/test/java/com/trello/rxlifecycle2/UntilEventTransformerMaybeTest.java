package com.trello.rxlifecycle2;

import io.reactivex.Maybe;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;

public class UntilEventTransformerMaybeTest {

    PublishSubject<String> subject;
    Maybe<String> maybe;
    PublishSubject<String> lifecycle;

    @Before
    public void setup() {
        subject =  PublishSubject.create();
        maybe = subject.firstElement();
        lifecycle = PublishSubject.create();
    }

    @Test
    public void noEvents() {
        TestObserver<String> testObserver = maybe
            .compose(new UntilEventTransformer<String, String>(lifecycle, "stop"))
            .test();

        subject.onNext("1");
        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void oneWrongEvent() {
        TestObserver<String> testObserver = maybe
            .compose(new UntilEventTransformer<String, String>(lifecycle, "stop"))
            .test();

        lifecycle.onNext("keep going");
        subject.onNext("1");
        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void twoEvents() {
        TestObserver<String> testObserver = maybe
            .compose(new UntilEventTransformer<String, String>(lifecycle, "stop"))
            .test();

        lifecycle.onNext("keep going");
        lifecycle.onNext("stop");

        subject.onNext("1");
        testObserver.assertNoValues();
        testObserver.assertComplete();
    }

}