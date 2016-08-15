/**
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

package com.trello.rxlifecycle;

import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RxLifecycleTest {

    private Observable<Object> observable;

    @Before
    public void setup() {
        // Simulate an actual lifecycle (hot Observable that does not end)
        observable = PublishSubject.create().asObservable();
    }

    @Test
    public void testBindLifecycle() {
        BehaviorSubject<Object> lifecycle = BehaviorSubject.create();
        Subscription attachSub = observable.compose(RxLifecycle.bind(lifecycle)).subscribe();
        assertFalse(attachSub.isUnsubscribed());
        lifecycle.onNext(new Object());
        assertTrue(attachSub.isUnsubscribed());
    }

    @Test
    public void testBindLifecycleOtherObject() {
        // Ensures it works with other types as well, and not just "Object"
        BehaviorSubject<String> lifecycle = BehaviorSubject.create();
        Subscription attachSub = observable.compose(RxLifecycle.bind(lifecycle)).subscribe();
        assertFalse(attachSub.isUnsubscribed());
        lifecycle.onNext("");
        assertTrue(attachSub.isUnsubscribed());
    }

    // Null checks

    @Test(expected=NullPointerException.class)
    public void testBindThrowsOnNullLifecycle() {
        //noinspection ResourceType
        RxLifecycle.bind((Observable) null);
    }
}
