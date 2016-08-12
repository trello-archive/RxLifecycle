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

package com.trello.rxlifecycle.components;

import com.trello.navi.Event;
import com.trello.navi.NaviComponent;
import com.trello.navi.internal.NaviEmitter;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import java.lang.ref.WeakReference;
import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class NaviFragmentLifecycleTest {

    @Test
    public void testLifecycle() {
        NaviEmitter fragment = NaviEmitter.createFragmentEmitter();
        LifecycleProvider<FragmentEvent> provider = NaviLifecycle.createFragmentLifecycleProvider(fragment);

        TestSubscriber<FragmentEvent> testSubscriber = new TestSubscriber<>();
        provider.lifecycle().subscribe(testSubscriber);

        fragment.onAttach(null);
        fragment.onCreate(null);
        fragment.onCreateView(null);
        fragment.onStart();
        fragment.onResume();
        fragment.onPause();
        fragment.onStop();
        fragment.onDestroyView();
        fragment.onDestroy();
        fragment.onDetach();

        testSubscriber.assertValues(
            FragmentEvent.ATTACH,
            FragmentEvent.CREATE,
            FragmentEvent.CREATE_VIEW,
            FragmentEvent.START,
            FragmentEvent.RESUME,
            FragmentEvent.PAUSE,
            FragmentEvent.STOP,
            FragmentEvent.DESTROY_VIEW,
            FragmentEvent.DESTROY,
            FragmentEvent.DETACH
        );
    }

    @Test
    public void testBindUntilEvent() {
        NaviEmitter fragment = NaviEmitter.createFragmentEmitter();
        LifecycleProvider<FragmentEvent> provider = NaviLifecycle.createFragmentLifecycleProvider(fragment);

        Observable<Object> observable = PublishSubject.create().asObservable();
        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        observable.compose(provider.bindUntilEvent(FragmentEvent.STOP)).subscribe(testSubscriber);

        fragment.onAttach(null);
        assertFalse(testSubscriber.isUnsubscribed());
        fragment.onCreate(null);
        assertFalse(testSubscriber.isUnsubscribed());
        fragment.onCreate(null);
        assertFalse(testSubscriber.isUnsubscribed());
        fragment.onStart();
        assertFalse(testSubscriber.isUnsubscribed());
        fragment.onResume();
        assertFalse(testSubscriber.isUnsubscribed());
        fragment.onPause();
        assertFalse(testSubscriber.isUnsubscribed());
        fragment.onStop();
        testSubscriber.assertCompleted();
        testSubscriber.assertUnsubscribed();
    }

    @Test
    public void testBindToLifecycle() {
        NaviEmitter fragment = NaviEmitter.createFragmentEmitter();
        LifecycleProvider<FragmentEvent> provider = NaviLifecycle.createFragmentLifecycleProvider(fragment);

        Observable<Object> observable = PublishSubject.create().asObservable();

        fragment.onAttach(null);
        TestSubscriber<Object> attachTestSub = new TestSubscriber<>();
        observable.compose(provider.bindToLifecycle()).subscribe(attachTestSub);

        fragment.onCreate(null);
        assertFalse(attachTestSub.isUnsubscribed());
        TestSubscriber<Object> createTestSub = new TestSubscriber<>();
        observable.compose(provider.bindToLifecycle()).subscribe(createTestSub);

        fragment.onCreateView(null);
        assertFalse(attachTestSub.isUnsubscribed());
        assertFalse(createTestSub.isUnsubscribed());
        TestSubscriber<Object> createViewTestSub = new TestSubscriber<>();
        observable.compose(provider.bindToLifecycle()).subscribe(createViewTestSub);

        fragment.onStart();
        assertFalse(attachTestSub.isUnsubscribed());
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(createViewTestSub.isUnsubscribed());
        TestSubscriber<Object> startTestSub = new TestSubscriber<>();
        observable.compose(provider.bindToLifecycle()).subscribe(startTestSub);

        fragment.onResume();
        assertFalse(attachTestSub.isUnsubscribed());
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(createViewTestSub.isUnsubscribed());
        assertFalse(startTestSub.isUnsubscribed());
        TestSubscriber<Object> resumeTestSub = new TestSubscriber<>();
        observable.compose(provider.bindToLifecycle()).subscribe(resumeTestSub);

        fragment.onPause();
        assertFalse(attachTestSub.isUnsubscribed());
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(createViewTestSub.isUnsubscribed());
        assertFalse(startTestSub.isUnsubscribed());
        resumeTestSub.assertCompleted();
        resumeTestSub.assertUnsubscribed();
        TestSubscriber<Object> pauseTestSub = new TestSubscriber<>();
        observable.compose(provider.bindToLifecycle()).subscribe(pauseTestSub);

        fragment.onStop();
        assertFalse(attachTestSub.isUnsubscribed());
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(createViewTestSub.isUnsubscribed());
        startTestSub.assertCompleted();
        startTestSub.assertUnsubscribed();
        pauseTestSub.assertCompleted();
        pauseTestSub.assertUnsubscribed();
        TestSubscriber<Object> stopTestSub = new TestSubscriber<>();
        observable.compose(provider.bindToLifecycle()).subscribe(stopTestSub);

        fragment.onDestroyView();
        assertFalse(attachTestSub.isUnsubscribed());
        assertFalse(createTestSub.isUnsubscribed());
        createViewTestSub.assertCompleted();
        createViewTestSub.assertUnsubscribed();
        stopTestSub.assertCompleted();
        stopTestSub.assertUnsubscribed();
        TestSubscriber<Object> destroyViewTestSub = new TestSubscriber<>();
        observable.compose(provider.bindToLifecycle()).subscribe(destroyViewTestSub);

        fragment.onDestroy();
        assertFalse(attachTestSub.isUnsubscribed());
        createTestSub.assertCompleted();
        createTestSub.assertUnsubscribed();
        destroyViewTestSub.assertCompleted();
        destroyViewTestSub.assertUnsubscribed();
        TestSubscriber<Object> destroyTestSub = new TestSubscriber<>();
        observable.compose(provider.bindToLifecycle()).subscribe(destroyTestSub);

        fragment.onDetach();
        attachTestSub.assertCompleted();
        attachTestSub.assertUnsubscribed();
        destroyTestSub.assertCompleted();
        destroyTestSub.assertUnsubscribed();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadHandler() {
        HashSet<Event<?>> notEnoughEvents = new HashSet<>();
        notEnoughEvents.add(Event.ATTACH);
        NaviComponent badHandler = new NaviEmitter(notEnoughEvents);
        //noinspection CheckResult
        NaviLifecycle.createFragmentLifecycleProvider(badHandler);
    }

    @Test
    public void testPersistance() {
        NaviEmitter fragment = NaviEmitter.createFragmentEmitter();
        LifecycleProvider<FragmentEvent> provider = NaviLifecycle.createFragmentLifecycleProvider(fragment);

        TestSubscriber<FragmentEvent> testSubscriber = new TestSubscriber<>();
        provider.lifecycle().subscribe(testSubscriber);

        fragment.onAttach(null);
        fragment.onCreate(null);
        fragment.onCreateView(null);
        fragment.onStart();
        fragment.onResume();
        fragment.onPause();
        fragment.onStop();
        fragment.onDestroyView();
        fragment.onDestroy();
        fragment.onDetach();

        // Verify that you can remain subscribed until the Fragment is completely gone
        fragment.onAttach(null);

        testSubscriber.assertValues(
            FragmentEvent.ATTACH,
            FragmentEvent.CREATE,
            FragmentEvent.CREATE_VIEW,
            FragmentEvent.START,
            FragmentEvent.RESUME,
            FragmentEvent.PAUSE,
            FragmentEvent.STOP,
            FragmentEvent.DESTROY_VIEW,
            FragmentEvent.DESTROY,
            FragmentEvent.DETACH,
            FragmentEvent.ATTACH
        );
    }

    @Test
    public void testLeakFree() {
        NaviEmitter fragment = NaviEmitter.createFragmentEmitter();
        LifecycleProvider<FragmentEvent> provider = NaviLifecycle.createFragmentLifecycleProvider(fragment);
        WeakReference<NaviEmitter> fragmentRef = new WeakReference<>(fragment);
        WeakReference<LifecycleProvider<FragmentEvent>> providerRef = new WeakReference<>(provider);

        Observable<Object> observable = PublishSubject.create().asObservable();
        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        observable.compose(provider.bindUntilEvent(FragmentEvent.STOP)).subscribe(testSubscriber);

        fragment.onAttach(null);
        fragment.onCreate(null);
        fragment.onCreateView(null);
        fragment.onStart();
        fragment.onResume();
        fragment.onPause();
        fragment.onStop();
        fragment.onDestroyView();
        fragment.onDestroy();
        fragment.onDetach();

        fragment = null;
        provider = null;
        TestUtil.cleanGarbage();

        assertNull(fragmentRef.get());
        assertNull(providerRef.get());
    }
}
