package com.trello.rxlifecycle;

import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CancellationException;

public class UntilLifecycleTransformerCompletableTest {

    PublishSubject<Object> subject;
    Completable completable;
    PublishSubject<String> lifecycle;

    @Before
    public void setup() {
        subject = PublishSubject.create();
        completable = Completable.fromObservable(subject);
        lifecycle = PublishSubject.create();
    }

    @Test
    public void noEvent() {
        TestObserver<Void> testObserver = completable
            .compose(new UntilLifecycleTransformer<String, String>(lifecycle))
            .test();

        subject.onComplete();

        testObserver.assertComplete();
    }

    @Test
    public void oneEvent() {
        TestObserver<Void> testObserver = completable
            .compose(new UntilLifecycleTransformer<String, String>(lifecycle))
            .test();

        lifecycle.onNext("stop");
        subject.onComplete();

        testObserver.assertError(CancellationException.class);
    }
}