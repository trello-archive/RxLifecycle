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

package com.trello.rxlifecycle2.components.preference;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.trello.rxlifecycle2.android.FragmentEvent.STOP;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxPreferenceFragmentLifecycleTest {

    private Observable<Object> observable;

    @Before
    public void setup() {
        observable = PublishSubject.create().hide();
    }

    @Test
    public void testRxPreferenceFragmentCompat() {
        // Requires android.support.v7.preference.R.preferenceTheme
        // attribute being set.
        //
        // testLifecycle(new TestRxPreferenceFragmentCompat());
        // testBindUntilEvent(new TestRxPreferenceFragmentCompat());
        // testBindToLifecycle(new TestRxPreferenceFragmentCompat());
    }

    private void testLifecycle(LifecycleProvider<FragmentEvent> provider) {
        Fragment fragment = (Fragment) provider;
        startFragment(fragment);

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

        TestObserver<Object> testObserver = observable.compose(provider.bindUntilEvent(STOP)).test();

        fragment.onAttach(null);
        testObserver.assertNotComplete();
        fragment.onCreate(null);
        testObserver.assertNotComplete();
        fragment.onViewCreated(null, null);
        testObserver.assertNotComplete();
        fragment.onStart();
        testObserver.assertNotComplete();
        fragment.onResume();
        testObserver.assertNotComplete();
        fragment.onPause();
        testObserver.assertNotComplete();
        fragment.onStop();
        testObserver.assertComplete();
    }

    // Tests bindToLifecycle for any given LifecycleProvider<FragmentEvent> implementation
    private void testBindToLifecycle(LifecycleProvider<FragmentEvent> provider) {
        Fragment fragment = (Fragment) provider;
        startFragment(fragment);

        fragment.onAttach(null);
        TestObserver<Object> attachObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onCreate(null);
        attachObserver.assertNotComplete();
        TestObserver<Object> createObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onViewCreated(null, null);
        attachObserver.assertNotComplete();
        createObserver.assertNotComplete();
        TestObserver<Object> createViewObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onStart();
        attachObserver.assertNotComplete();
        createObserver.assertNotComplete();
        createViewObserver.assertNotComplete();
        TestObserver<Object> startObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onResume();
        attachObserver.assertNotComplete();
        createObserver.assertNotComplete();
        createViewObserver.assertNotComplete();
        startObserver.assertNotComplete();
        TestObserver<Object> resumeObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onPause();
        attachObserver.assertNotComplete();
        createObserver.assertNotComplete();
        createViewObserver.assertNotComplete();
        startObserver.assertNotComplete();
        resumeObserver.assertComplete();
        TestObserver<Object> pauseObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onStop();
        attachObserver.assertNotComplete();
        createObserver.assertNotComplete();
        createViewObserver.assertNotComplete();
        startObserver.assertComplete();
        pauseObserver.assertComplete();
        TestObserver<Object> stopObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onDestroyView();
        attachObserver.assertNotComplete();
        createObserver.assertNotComplete();
        createViewObserver.assertComplete();
        stopObserver.assertComplete();
        TestObserver<Object> destroyViewObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onDestroy();
        attachObserver.assertNotComplete();
        createObserver.assertComplete();
        destroyViewObserver.assertComplete();
        TestObserver<Object> destroyObserver = observable.compose(provider.bindToLifecycle()).test();

        fragment.onDetach();
        attachObserver.assertComplete();
        destroyObserver.assertComplete();
    }

    // Easier than making everyone create their own shadows
    private void startFragment(Fragment fragment) {
        Robolectric.setupActivity(FragmentActivity.class).getSupportFragmentManager()
            .beginTransaction()
            .add(fragment, null)
            .commit();
    }

    // These classes are just for testing since components are abstract

    public static class TestRxPreferenceFragmentCompat extends RxPreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
        }
    }
}
