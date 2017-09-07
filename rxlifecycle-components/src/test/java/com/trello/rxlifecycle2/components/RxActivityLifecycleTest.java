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

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxFragmentActivity;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import static com.trello.rxlifecycle2.android.ActivityEvent.STOP;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxActivityLifecycleTest {

    private Observable<Object> observable;

    @Before
    public void setup() {
        observable = PublishSubject.create().hide();
    }

    @Test
    public void testRxActivity() {
        testLifecycle(Robolectric.buildActivity(TestRxActivity.class));
        testBindUntilEvent(Robolectric.buildActivity(TestRxActivity.class));
        testBindToLifecycle(Robolectric.buildActivity(TestRxActivity.class));
    }

    @Test
    public void testRxFragmentActivity() {
        testLifecycle(Robolectric.buildActivity(TestRxFragmentActivity.class));
        testBindUntilEvent(Robolectric.buildActivity(TestRxFragmentActivity.class));
        testBindToLifecycle(Robolectric.buildActivity(TestRxFragmentActivity.class));
    }

    @Test
    public void testRxAppCompatActivity() {
        // TODO: Doesn't work due to https://github.com/robolectric/robolectric/issues/1796
        //
        // testBindUntilEvent(Robolectric.buildActivity(RxAppCompatActivity.class));
        // testBindToLifecycle(Robolectric.buildActivity(RxAppCompatActivity.class));
    }

    private void testLifecycle(ActivityController<? extends LifecycleProvider<ActivityEvent>> controller) {
        LifecycleProvider<ActivityEvent> activity = controller.get();

        TestObserver<ActivityEvent> testObserver = activity.lifecycle().test();

        controller.create();
        controller.start();
        controller.resume();
        controller.pause();
        controller.stop();
        controller.destroy();

        testObserver.assertValues(
            ActivityEvent.CREATE,
            ActivityEvent.START,
            ActivityEvent.RESUME,
            ActivityEvent.PAUSE,
            ActivityEvent.STOP,
            ActivityEvent.DESTROY
        );
    }

    // Tests bindUntil for any given RxActivityLifecycle implementation
    private void testBindUntilEvent(ActivityController<? extends LifecycleProvider<ActivityEvent>> controller) {
        LifecycleProvider<ActivityEvent> activity = controller.get();

        TestObserver<Object> testObserver = observable.compose(activity.bindUntilEvent(STOP)).test();

        controller.create();
        testObserver.assertNotComplete();
        controller.start();
        testObserver.assertNotComplete();
        controller.resume();
        testObserver.assertNotComplete();
        controller.pause();
        testObserver.assertNotComplete();
        controller.stop();
        testObserver.assertComplete();
    }

    // Tests bindToLifecycle for any given RxActivityLifecycle implementation
    private void testBindToLifecycle(ActivityController<? extends LifecycleProvider<ActivityEvent>> controller) {
        LifecycleProvider<ActivityEvent> activity = controller.get();

        controller.create();
        TestObserver<Object> createObserver = observable.compose(activity.bindToLifecycle()).test();

        controller.start();
        createObserver.assertNotComplete();
        TestObserver<Object> startObserver = observable.compose(activity.bindToLifecycle()).test();

        controller.resume();
        createObserver.assertNotComplete();
        startObserver.assertNotComplete();
        TestObserver<Object> resumeObserver = observable.compose(activity.bindToLifecycle()).test();

        controller.pause();
        createObserver.assertNotComplete();
        startObserver.assertNotComplete();
        resumeObserver.assertComplete();
        TestObserver<Object> pauseObserver = observable.compose(activity.bindToLifecycle()).test();

        controller.stop();
        createObserver.assertNotComplete();
        startObserver.assertComplete();
        pauseObserver.assertComplete();
        TestObserver<Object> stopObserver = observable.compose(activity.bindToLifecycle()).test();

        controller.destroy();
        createObserver.assertComplete();
        stopObserver.assertComplete();
    }

    // These classes are just for testing since components are abstract

    public static class TestRxActivity extends RxActivity {
    }

    public static class TestRxFragmentActivity extends RxFragmentActivity{
    }
}
