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

import android.os.Bundle;
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

import static com.trello.navi2.internal.NaviEmitter.createFragmentEmitter;
import static com.trello.rxlifecycle2.android.FragmentEvent.STOP;
import static com.trello.rxlifecycle2.navi.NaviLifecycle.createFragmentLifecycleProvider;
import static org.junit.Assert.assertNull;

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
    public void testNonLifecycleEvents() {
        NaviEmitter fragment = NaviEmitter.createFragmentEmitter();
        LifecycleProvider<FragmentEvent> provider = NaviLifecycle.createFragmentLifecycleProvider(fragment);
        TestObserver<FragmentEvent> testObserver = provider.lifecycle().test();
        fragment.onRestoreInstanceState(new Bundle());
        testObserver.assertNoValues();
        testObserver.assertNoErrors();
    }

    @Test
    public void testBindUntilEvent() {
        NaviEmitter fragment = createFragmentEmitter();
        LifecycleProvider<FragmentEvent> provider = createFragmentLifecycleProvider(fragment);

        PublishSubject<Object> stream = PublishSubject.create();
        Observable<Object> observable = stream.hide();
        TestObserver<Object> testObserver = observable.compose(provider.bindUntilEvent(STOP)).test();

        fragment.onAttach(null);
        stream.onNext("attach");
        testObserver.assertNotComplete();
        fragment.onCreate(null);
        stream.onNext("create");
        testObserver.assertNotComplete();
        fragment.onCreateView(null);
        stream.onNext("createView");
        testObserver.assertNotComplete();
        fragment.onStart();
        stream.onNext("start");
        testObserver.assertNotComplete();
        fragment.onResume();
        stream.onNext("resume");
        testObserver.assertNotComplete();
        fragment.onPause();
        stream.onNext("pause");
        testObserver.assertNotComplete();
        fragment.onStop();
        stream.onNext("stop");
        testObserver.assertValues("attach", "create", "createView", "start", "resume", "pause");
        testObserver.assertNotComplete();
    }

    @Test
    public void testBindToLifecycle() {
        NaviEmitter fragment = createFragmentEmitter();
        LifecycleProvider<FragmentEvent> provider = createFragmentLifecycleProvider(fragment);

        PublishSubject<Object> stream = PublishSubject.create();
        Observable<Object> observable = stream.hide();

        fragment.onAttach(null);
        TestObserver<Object> attachObserver = stream.hide().compose(provider.bindToLifecycle()).test();
        stream.onNext("attach");

        fragment.onCreate(null);
        TestObserver<Object> createObserver = stream.hide().compose(provider.bindToLifecycle()).test();
        stream.onNext("create");

        fragment.onCreateView(null);
        TestObserver<Object> createViewObserver = stream.hide().compose(provider.bindToLifecycle()).test();
        stream.onNext("createView");

        fragment.onStart();
        TestObserver<Object> startObserver = stream.hide().compose(provider.bindToLifecycle()).test();
        stream.onNext("start");

        fragment.onResume();
        TestObserver<Object> resumeObserver = stream.hide().compose(provider.bindToLifecycle()).test();
        stream.onNext("resume");

        fragment.onPause();
        TestObserver<Object> pauseObserver = stream.hide().compose(provider.bindToLifecycle()).test();
        stream.onNext("pause");
        resumeObserver.assertNotComplete();
        resumeObserver.assertValues("resume");

        fragment.onStop();
        TestObserver<Object> stopObserver = stream.hide().compose(provider.bindToLifecycle()).test();
        stream.onNext("stop");
        startObserver.assertNotComplete();
        startObserver.assertValues("start", "resume", "pause");
        pauseObserver.assertNotComplete();
        pauseObserver.assertValues("pause");

        fragment.onDestroyView();
        TestObserver<Object> destroyViewObserver = stream.hide().compose(provider.bindToLifecycle()).test();
        stream.onNext("destroyView");
        createViewObserver.assertNotComplete();
        createViewObserver.assertValues("createView", "start", "resume", "pause", "stop");
        stopObserver.assertNotComplete();
        stopObserver.assertValues("stop");

        fragment.onDestroy();
        TestObserver<Object> destroyObserver = stream.hide().compose(provider.bindToLifecycle()).test();
        stream.onNext("destroy");
        createObserver.assertNotComplete();
        createObserver.assertValues("create", "createView", "start", "resume", "pause", "stop", "destroyView");
        destroyViewObserver.assertNotComplete();
        destroyViewObserver.assertValues("destroyView");

        fragment.onDetach();
        stream.onNext("detach");
        attachObserver.assertNotComplete();
        attachObserver.assertValues("attach", "create", "createView", "start", "resume",
                "pause", "stop", "destroyView", "destroy");
        destroyObserver.assertNotComplete();
        destroyObserver.assertValues("destroy");
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
