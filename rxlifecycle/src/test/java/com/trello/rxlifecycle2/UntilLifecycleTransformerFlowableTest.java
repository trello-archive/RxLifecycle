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

import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.Test;

public class UntilLifecycleTransformerFlowableTest {

    PublishProcessor<String> stream;
    PublishSubject<String> lifecycle;

    @Before
    public void setup() {
        stream = PublishProcessor.create();
        lifecycle = PublishSubject.create();
    }

    @Test
    public void noEvent() {
        TestSubscriber<String> testSubscriber = stream
            .compose(RxLifecycle.<String, String>bind(lifecycle))
            .test();

        stream.onNext("1");
        stream.onNext("2");
        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNotTerminated();
    }

    @Test
    public void oneEvent() {
        TestSubscriber<String> testSubscriber = stream
            .compose(RxLifecycle.<String, String>bind(lifecycle))
            .test();

        stream.onNext("1");
        lifecycle.onNext("stop");
        stream.onNext("2");

        testSubscriber.assertValues("1");
        testSubscriber.assertComplete();
    }
}