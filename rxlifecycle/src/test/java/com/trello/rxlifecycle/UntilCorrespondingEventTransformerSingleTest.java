package com.trello.rxlifecycle;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

public class UntilCorrespondingEventTransformerSingleTest {

    PublishSubject<String> lifecycle;
    TestScheduler testScheduler; // Since Single is not backpressure aware, use this to simulate it taking time

    @Before
    public void setup() {
        lifecycle = PublishSubject.create();
        testScheduler = new TestScheduler();
    }

    @Test
    public void noEvents() {
        TestObserver<String> testObserver = Single.just("1")
            .delay(1, TimeUnit.MILLISECONDS, testScheduler)
            .compose(new UntilCorrespondingEventTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .test();

        testObserver.assertNoValues();

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void oneStartEvent() {
        TestObserver<String> testObserver = Single.just("1")
            .delay(1, TimeUnit.MILLISECONDS, testScheduler)
            .compose(new UntilCorrespondingEventTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .test();

        testObserver.assertNoValues();

        lifecycle.onNext("create");
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void twoOpenEvents() {
        TestObserver<String> testObserver = Single.just("1")
            .delay(1, TimeUnit.MILLISECONDS, testScheduler)
            .compose(new UntilCorrespondingEventTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .test();

        testObserver.assertNoValues();

        lifecycle.onNext("create");
        lifecycle.onNext("start");
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void openAndCloseEvent() {
        TestObserver<String> testObserver = Single.just("1")
            .delay(1, TimeUnit.MILLISECONDS, testScheduler)
            .compose(new UntilCorrespondingEventTransformer<String, String>(lifecycle, CORRESPONDING_EVENTS))
            .test();

        lifecycle.onNext("create");
        testObserver.assertNoErrors();

        lifecycle.onNext("destroy");
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        testObserver.assertNoValues();
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