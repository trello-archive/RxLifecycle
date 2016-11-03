package com.trello.rxlifecycle;

import io.reactivex.Completable;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CancellationException;

public class UntilCorrespondingEventTransformerCompletableTest {

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
            .compose(new UntilCorrespondingEventTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .test();

        subject.onComplete();
        testObserver.assertComplete();
    }

    @Test
    public void oneStartEvent() {
        TestObserver<Void> testObserver = completable
            .compose(new UntilCorrespondingEventTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .test();

        lifecycle.onNext("create");
        subject.onComplete();
        testObserver.assertComplete();
    }

    @Test
    public void twoOpenEvents() {
        TestObserver<Void> testObserver = completable
            .compose(new UntilCorrespondingEventTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .test();

        lifecycle.onNext("create");
        lifecycle.onNext("start");
        subject.onComplete();
        testObserver.assertComplete();
    }

    @Test
    public void openAndCloseEvent() {
        TestObserver<Void> testObserver = completable
            .compose(new UntilCorrespondingEventTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .test();

        lifecycle.onNext("create");
        lifecycle.onNext("destroy");
        subject.onComplete();
        testObserver.assertError(CancellationException.class);
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