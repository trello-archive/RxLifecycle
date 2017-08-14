/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trello.rxlifecycle2;

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
            .compose(RxLifecycle.bind(lifecycle, CORRESPONDING_EVENTS))
            .test();

        subject.onComplete();
        testObserver.assertComplete();
    }

    @Test
    public void oneStartEvent() {
        TestObserver<Void> testObserver = completable
            .compose(RxLifecycle.bind(lifecycle, CORRESPONDING_EVENTS))
            .test();

        lifecycle.onNext("create");
        subject.onComplete();
        testObserver.assertComplete();
    }

    @Test
    public void twoOpenEvents() {
        TestObserver<Void> testObserver = completable
            .compose(RxLifecycle.bind(lifecycle, CORRESPONDING_EVENTS))
            .test();

        lifecycle.onNext("create");
        lifecycle.onNext("start");
        subject.onComplete();
        testObserver.assertComplete();
    }

    @Test
    public void openAndCloseEvent() {
        TestObserver<Void> testObserver = completable
            .compose(RxLifecycle.bind(lifecycle, CORRESPONDING_EVENTS))
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