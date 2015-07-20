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

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxFragmentActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxActivityLifecycleTest {

    private Observable<Object> observable;

    @Before
    public void setup() {
        observable = PublishSubject.create().asObservable();
    }

    @Test
    public void testRxActivity() {
        testBindUntilEvent(Robolectric.buildActivity(RxActivity.class));
        testBindToLifecycle(Robolectric.buildActivity(RxActivity.class));
    }

    @Test
    public void testRxFragmentActivity() {
        testBindUntilEvent(Robolectric.buildActivity(RxFragmentActivity.class));
        testBindToLifecycle(Robolectric.buildActivity(RxFragmentActivity.class));
    }

    @Test
    public void testRxAppCompatActivity() {
        // TODO: Doesn't work due to https://github.com/robolectric/robolectric/issues/1796
        //
        // testBindUntilEvent(Robolectric.buildActivity(RxAppCompatActivity.class));
        // testBindToLifecycle(Robolectric.buildActivity(RxAppCompatActivity.class));
    }

    // Tests bindUntil for any given RxActivityLifecycle implementation
    void testBindUntilEvent(ActivityController<? extends ActivityLifecycleProvider> controller) {
        ActivityLifecycleProvider activity = controller.get();

        Subscription untilStop = observable.compose(activity.bindUntilEvent(ActivityEvent.STOP)).subscribe();

        controller.create();
        assertFalse(untilStop.isUnsubscribed());
        controller.start();
        assertFalse(untilStop.isUnsubscribed());
        controller.resume();
        assertFalse(untilStop.isUnsubscribed());
        controller.pause();
        assertFalse(untilStop.isUnsubscribed());
        controller.stop();
        assertTrue(untilStop.isUnsubscribed());
    }

    // Tests bindToLifecycle for any given RxActivityLifecycle implementation
    void testBindToLifecycle(ActivityController<? extends ActivityLifecycleProvider> controller) {
        ActivityLifecycleProvider activity = controller.get();

        controller.create();
        Subscription createSub = observable.compose(activity.bindToLifecycle()).subscribe();

        controller.start();
        assertFalse(createSub.isUnsubscribed());
        Subscription startSub = observable.compose(activity.bindToLifecycle()).subscribe();

        controller.resume();
        assertFalse(createSub.isUnsubscribed());
        assertFalse(startSub.isUnsubscribed());
        Subscription resumeSub = observable.compose(activity.bindToLifecycle()).subscribe();

        controller.pause();
        assertFalse(createSub.isUnsubscribed());
        assertFalse(startSub.isUnsubscribed());
        assertTrue(resumeSub.isUnsubscribed());
        Subscription pauseSub = observable.compose(activity.bindToLifecycle()).subscribe();

        controller.stop();
        assertFalse(createSub.isUnsubscribed());
        assertTrue(startSub.isUnsubscribed());
        assertTrue(pauseSub.isUnsubscribed());
        Subscription stopSub = observable.compose(activity.bindToLifecycle()).subscribe();

        controller.destroy();
        assertTrue(createSub.isUnsubscribed());
        assertTrue(stopSub.isUnsubscribed());
    }
}
