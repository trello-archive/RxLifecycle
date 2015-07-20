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

package com.trello.rxlifecycle.components.support;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.components.FragmentLifecycleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxSupportFragmentLifecycleTest {

    private Observable<Object> observable;

    @Before
    public void setup() {
        observable = PublishSubject.create().asObservable();
    }

    @Test
    public void testRxFragment() {
        testBindUntilEvent(new RxFragment());
        testBindToLifecycle(new RxFragment());
    }

    @Test
    public void testRxDialogFragment() {
        testBindUntilEvent(new RxDialogFragment());
        testBindToLifecycle(new RxDialogFragment());
    }

    // Tests bindUntil for any given FragmentLifecycleProvider implementation
    void testBindUntilEvent(FragmentLifecycleProvider provider) {
        Fragment fragment = (Fragment) provider;
        startFragment(fragment);

        Subscription untilStop = observable.compose(provider.bindUntilEvent(FragmentEvent.STOP)).subscribe();

        fragment.onAttach(null);
        assertFalse(untilStop.isUnsubscribed());
        fragment.onCreate(null);
        assertFalse(untilStop.isUnsubscribed());
        fragment.onViewCreated(null, null);
        assertFalse(untilStop.isUnsubscribed());
        fragment.onStart();
        assertFalse(untilStop.isUnsubscribed());
        fragment.onResume();
        assertFalse(untilStop.isUnsubscribed());
        fragment.onPause();
        assertFalse(untilStop.isUnsubscribed());
        fragment.onStop();
        assertTrue(untilStop.isUnsubscribed());
    }

    // Tests bindToLifecycle for any given FragmentLifecycleProvider implementation
    void testBindToLifecycle(FragmentLifecycleProvider provider) {
        Fragment fragment = (Fragment) provider;
        startFragment(fragment);

        fragment.onAttach(null);
        Subscription attachSub = observable.compose(provider.bindToLifecycle()).subscribe();

        fragment.onCreate(null);
        assertFalse(attachSub.isUnsubscribed());
        Subscription createSub = observable.compose(provider.bindToLifecycle()).subscribe();

        fragment.onViewCreated(null, null);
        assertFalse(attachSub.isUnsubscribed());
        assertFalse(createSub.isUnsubscribed());
        Subscription createViewSub = observable.compose(provider.bindToLifecycle()).subscribe();

        fragment.onStart();
        assertFalse(attachSub.isUnsubscribed());
        assertFalse(createSub.isUnsubscribed());
        assertFalse(createViewSub.isUnsubscribed());
        Subscription startSub = observable.compose(provider.bindToLifecycle()).subscribe();

        fragment.onResume();
        assertFalse(attachSub.isUnsubscribed());
        assertFalse(createSub.isUnsubscribed());
        assertFalse(createViewSub.isUnsubscribed());
        assertFalse(startSub.isUnsubscribed());
        Subscription resumeSub = observable.compose(provider.bindToLifecycle()).subscribe();

        fragment.onPause();
        assertFalse(attachSub.isUnsubscribed());
        assertFalse(createSub.isUnsubscribed());
        assertFalse(createViewSub.isUnsubscribed());
        assertFalse(startSub.isUnsubscribed());
        assertTrue(resumeSub.isUnsubscribed());
        Subscription pauseSub = observable.compose(provider.bindToLifecycle()).subscribe();

        fragment.onStop();
        assertFalse(attachSub.isUnsubscribed());
        assertFalse(createSub.isUnsubscribed());
        assertFalse(createViewSub.isUnsubscribed());
        assertTrue(startSub.isUnsubscribed());
        assertTrue(pauseSub.isUnsubscribed());
        Subscription stopSub = observable.compose(provider.bindToLifecycle()).subscribe();

        fragment.onDestroyView();
        assertFalse(attachSub.isUnsubscribed());
        assertFalse(createSub.isUnsubscribed());
        assertTrue(createViewSub.isUnsubscribed());
        assertTrue(stopSub.isUnsubscribed());
        Subscription destroyViewSub = observable.compose(provider.bindToLifecycle()).subscribe();

        fragment.onDestroy();
        assertFalse(attachSub.isUnsubscribed());
        assertTrue(createSub.isUnsubscribed());
        assertTrue(destroyViewSub.isUnsubscribed());
        Subscription destroySub = observable.compose(provider.bindToLifecycle()).subscribe();

        fragment.onDetach();
        assertTrue(attachSub.isUnsubscribed());
        assertTrue(destroySub.isUnsubscribed());
    }

    // Easier than making everyone create their own shadows
    void startFragment(Fragment fragment) {
        Robolectric.setupActivity(FragmentActivity.class).getSupportFragmentManager()
            .beginTransaction()
            .add(fragment, null)
            .commit();
    }
}
