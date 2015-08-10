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
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static org.junit.Assert.assertFalse;

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

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        observable.compose(activity.bindUntilEvent(ActivityEvent.STOP)).subscribe(testSubscriber);

        controller.create();
        assertFalse(testSubscriber.isUnsubscribed());
        controller.start();
        assertFalse(testSubscriber.isUnsubscribed());
        controller.resume();
        assertFalse(testSubscriber.isUnsubscribed());
        controller.pause();
        assertFalse(testSubscriber.isUnsubscribed());
        controller.stop();
        testSubscriber.assertCompleted();
        testSubscriber.assertUnsubscribed();
    }

    // Tests bindToLifecycle for any given RxActivityLifecycle implementation
    void testBindToLifecycle(ActivityController<? extends ActivityLifecycleProvider> controller) {
        ActivityLifecycleProvider activity = controller.get();

        controller.create();
        TestSubscriber<Object> createTestSub = new TestSubscriber<>();
        observable.compose(activity.bindToLifecycle()).subscribe(createTestSub);

        controller.start();
        assertFalse(createTestSub.isUnsubscribed());
        TestSubscriber<Object> startTestSub = new TestSubscriber<>();
        observable.compose(activity.bindToLifecycle()).subscribe(startTestSub);

        controller.resume();
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(startTestSub.isUnsubscribed());
        TestSubscriber<Object> resumeTestSub = new TestSubscriber<>();
        observable.compose(activity.bindToLifecycle()).subscribe(resumeTestSub);

        controller.pause();
        assertFalse(createTestSub.isUnsubscribed());
        assertFalse(startTestSub.isUnsubscribed());
        resumeTestSub.assertCompleted();
        resumeTestSub.assertUnsubscribed();
        TestSubscriber<Object> pauseTestSub = new TestSubscriber<>();
        observable.compose(activity.bindToLifecycle()).subscribe(pauseTestSub);

        controller.stop();
        assertFalse(createTestSub.isUnsubscribed());
        startTestSub.assertCompleted();
        startTestSub.assertUnsubscribed();
        pauseTestSub.assertCompleted();
        pauseTestSub.assertUnsubscribed();
        TestSubscriber<Object> stopTestSub = new TestSubscriber<>();
        observable.compose(activity.bindToLifecycle()).subscribe(stopTestSub);

        controller.destroy();
        createTestSub.assertCompleted();
        createTestSub.assertUnsubscribed();
        stopTestSub.assertCompleted();
        stopTestSub.assertUnsubscribed();
    }
}
