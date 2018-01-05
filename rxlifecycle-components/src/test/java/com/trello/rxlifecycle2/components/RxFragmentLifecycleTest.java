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
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.FragmentTestUtil;

import static com.trello.rxlifecycle2.android.FragmentEvent.STOP;
import static org.robolectric.util.FragmentTestUtil.startFragment;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxFragmentLifecycleTest {

    private PublishSubject<Object> stream;

    @Before
    public void setup() {
        stream = PublishSubject.create();
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
        startFragment(fragment);

        TestObserver<Object> testObserver = stream.hide().compose(provider.bindUntilEvent(STOP)).test();

        fragment.onAttach(null);
        stream.onNext("attach");
        testObserver.assertNotComplete();
        fragment.onCreate(null);
        stream.onNext("create");
        testObserver.assertNotComplete();
        fragment.onViewCreated(null, null);
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

    // Tests bindToLifecycle for any given LifecycleProvider<FragmentEvent> implementation
    private void testBindToLifecycle(LifecycleProvider<FragmentEvent> provider) {
        Fragment fragment = (Fragment) provider;
        startFragment(fragment);

        fragment.onAttach(null);
        TestObserver<Object> attachObserver = stream.hide().compose(provider.bindToLifecycle()).test();
        stream.onNext("attach");

        fragment.onCreate(null);
        TestObserver<Object> createObserver = stream.hide().compose(provider.bindToLifecycle()).test();
        stream.onNext("create");

        fragment.onViewCreated(null, null);
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

    // These classes are just for testing since components are abstract

    public static class TestRxFragment extends RxFragment {
    }

    public static class TestRxDialogFragment extends RxDialogFragment {
    }

    public static class TestRxPreferenceFragment extends RxPreferenceFragment {
    }
}
