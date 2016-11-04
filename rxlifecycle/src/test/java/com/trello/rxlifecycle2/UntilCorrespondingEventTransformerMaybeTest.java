package com.trello.rxlifecycle2;

import io.reactivex.Maybe;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;

public class UntilCorrespondingEventTransformerMaybeTest {

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
            .compose(new UntilCorrespondingEventTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .test();

        subject.onNext("1");
        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void oneStartEvent() {
        TestObserver<String> testObserver = maybe
            .compose(new UntilCorrespondingEventTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .test();

        lifecycle.onNext("create");
        subject.onNext("1");
        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void twoOpenEvents() {
        TestObserver<String> testObserver = maybe
            .compose(new UntilCorrespondingEventTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .test();

        lifecycle.onNext("create");
        lifecycle.onNext("start");
        subject.onNext("1");
        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void openAndCloseEvent() {
        TestObserver<String> testObserver = maybe
            .compose(new UntilCorrespondingEventTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .test();

        lifecycle.onNext("create");
        lifecycle.onNext("destroy");
        subject.onNext("1");
        testObserver.assertNoValues();
        testObserver.assertComplete();
    }

    private static final Function<String, String> CORRESPONDING_EVENTS = new Function<String, String>() {
        @Override
        public String apply(String s) throws Exception {
            if (s.equals("create")) {
                return "destroy";
            }

            throw new IllegalArgumentException("Cannot handle: " + s);
        }
    };
}