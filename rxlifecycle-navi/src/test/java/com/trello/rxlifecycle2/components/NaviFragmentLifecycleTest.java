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

package com.trello.rxlifecycle2.components;

import com.trello.navi2.Event;
import com.trello.navi2.NaviComponent;
import com.trello.navi2.internal.NaviEmitter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.navi.NaviLifecycle;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Test;

import java.lang.ref.WeakReference;
import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class NaviFragmentLifecycleTest {

    @Test
    public void testLifecycle() {
        NaviEmitter fragment = NaviEmitter.createFragmentEmitter();
        LifecycleProvider<FragmentEvent> provider = NaviLifecycle.createFragmentLifecycleProvider(fragment);

        TestObserver<FragmentEvent> testObserver = provider.lifecycle().test();

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

        testObserver.assertValues(
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

        Observable<Object> observable = PublishSubject.create().hide();
        TestObserver<Object> testObserver = observable.compose(provider.bindUntilEvent(FragmentEvent.STOP)).test();

        fragment.onAttach(null);
        assertFalse(testObserver.isDisposed());
        fragment.onCreate(null);
        assertFalse(testObserver.isDisposed());
        fragment.onCreate(null);
        assertFalse(testObserver.isDisposed());
        fragment.onStart();
        assertFalse(testObserver.isDisposed());
        fragment.onResume();
        assertFalse(testObserver.isDisposed());
        fragment.onPause();
        assertFalse(testObserver.isDisposed());
        fragment.onStop();
        testObserver.assertComplete();
        assertTrue(testObserver.isDisposed());
    }

    @Test
    public void testBindToLifecycle() {
        NaviEmitter fragment = NaviEmitter.createFragmentEmitter();
        LifecycleProvider<FragmentEvent> provider = NaviLifecycle.createFragmentLifecycleProvider(fragment);

        Observable<Object> observable = PublishSubject.create().hide();

        fragment.onAttach(null);
        TestObserver<Object> attachObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onCreate(null);
        assertFalse(attachObserver.isDisposed());
        TestObserver<Object> createObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onCreateView(null);
        assertFalse(attachObserver.isDisposed());
        assertFalse(createObserver.isDisposed());
        TestObserver<Object> createViewObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onStart();
        assertFalse(attachObserver.isDisposed());
        assertFalse(createObserver.isDisposed());
        assertFalse(createViewObserver.isDisposed());
        TestObserver<Object> startObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onResume();
        assertFalse(attachObserver.isDisposed());
        assertFalse(createObserver.isDisposed());
        assertFalse(createViewObserver.isDisposed());
        assertFalse(startObserver.isDisposed());
        TestObserver<Object> resumeObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onPause();
        assertFalse(attachObserver.isDisposed());
        assertFalse(createObserver.isDisposed());
        assertFalse(createViewObserver.isDisposed());
        assertFalse(startObserver.isDisposed());
        resumeObserver.assertComplete();
        assertTrue(resumeObserver.isDisposed());
        TestObserver<Object> pauseObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onStop();
        assertFalse(attachObserver.isDisposed());
        assertFalse(createObserver.isDisposed());
        assertFalse(createViewObserver.isDisposed());
        startObserver.assertComplete();
        assertTrue(startObserver.isDisposed());
        pauseObserver.assertComplete();
        assertTrue(pauseObserver.isDisposed());
        TestObserver<Object> stopObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onDestroyView();
        assertFalse(attachObserver.isDisposed());
        assertFalse(createObserver.isDisposed());
        createViewObserver.assertComplete();
        assertTrue(createViewObserver.isDisposed());
        stopObserver.assertComplete();
        assertTrue(stopObserver.isDisposed());
        TestObserver<Object> destroyViewObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onDestroy();
        assertFalse(attachObserver.isDisposed());
        createObserver.assertComplete();
        assertTrue(createObserver.isDisposed());
        destroyViewObserver.assertComplete();
        assertTrue(destroyViewObserver.isDisposed());
        TestObserver<Object> destroyObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onDetach();
        attachObserver.assertComplete();
        assertTrue(attachObserver.isDisposed());
        destroyObserver.assertComplete();
        assertTrue(destroyObserver.isDisposed());
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

        TestObserver<FragmentEvent> testObserver = provider.lifecycle().test();

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

        testObserver.assertValues(
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

        Observable<Object> observable = PublishSubject.create().hide();
        TestObserver<Object> testObserver = observable.compose(provider.bindUntilEvent(FragmentEvent.STOP)).test();

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
