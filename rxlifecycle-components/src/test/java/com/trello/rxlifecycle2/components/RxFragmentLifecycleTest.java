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

import android.app.Fragment;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.FragmentTestUtil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxFragmentLifecycleTest {

    private Observable<Object> observable;

    @Before
    public void setup() {
        observable = PublishSubject.create().hide();
    }

    @Test
    public void testRxFragment() {
        testLifecycle(new TestRxFragment());
        testBindUntilEvent(new TestRxFragment());
        testBindToLifecycle(new TestRxFragment());
    }

    @Test
    public void testRxDialogFragment() {
        testLifecycle(new TestRxDialogFragment());
        testBindUntilEvent(new TestRxDialogFragment());
        testBindToLifecycle(new TestRxDialogFragment());
    }

    @Test
    public void testRxPreferenceFragment() {
        testLifecycle(new TestRxPreferenceFragment());
        testBindUntilEvent(new TestRxPreferenceFragment());
        testBindToLifecycle(new TestRxPreferenceFragment());
    }

    private void testLifecycle(LifecycleProvider<FragmentEvent> provider) {
        Fragment fragment = (Fragment) provider;
        FragmentTestUtil.startFragment(fragment);

        TestObserver<FragmentEvent> testObserver = provider.lifecycle().skip(1).test();

        fragment.onAttach(null);
        fragment.onCreate(null);
        fragment.onViewCreated(null, null);
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

    // Tests bindUntil for any given LifecycleProvider<FragmentEvent> implementation
    private void testBindUntilEvent(LifecycleProvider<FragmentEvent> provider) {
        Fragment fragment = (Fragment) provider;
        FragmentTestUtil.startFragment(fragment);

        TestObserver<Object> testObserver = observable.compose(provider.bindUntilEvent(FragmentEvent.STOP)).test();

        fragment.onAttach(null);
        assertFalse(testObserver.isDisposed());
        fragment.onCreate(null);
        assertFalse(testObserver.isDisposed());
        fragment.onViewCreated(null, null);
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

    // Tests bindToLifecycle for any given LifecycleProvider<FragmentEvent> implementation
    private void testBindToLifecycle(LifecycleProvider<FragmentEvent> provider) {
        Fragment fragment = (Fragment) provider;
        FragmentTestUtil.startFragment(fragment);

        fragment.onAttach(null);
        TestObserver<Object> attachObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onCreate(null);
        assertFalse(attachObserver.isDisposed());
        TestObserver<Object> createObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onViewCreated(null, null);
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

    // These classes are just for testing since components are abstract

    public static class TestRxFragment extends RxFragment {
    }

    public static class TestRxDialogFragment extends RxDialogFragment {
    }

    public static class TestRxPreferenceFragment extends RxPreferenceFragment {
    }
}
