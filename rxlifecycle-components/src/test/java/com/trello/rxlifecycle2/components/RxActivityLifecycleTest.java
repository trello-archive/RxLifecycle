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

    private PublishSubject<Object> stream;

    @Before
    public void setup() {
        stream = PublishSubject.create();
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

        TestObserver<Object> testObserver = stream.hide().compose(activity.bindUntilEvent(STOP)).test();

        controller.create();
        stream.onNext("create");
        testObserver.assertNotComplete();
        controller.start();
        stream.onNext("start");
        testObserver.assertNotComplete();
        controller.resume();
        stream.onNext("resume");
        testObserver.assertNotComplete();
        controller.pause();
        stream.onNext("pause");
        testObserver.assertNotComplete();
        controller.stop();
        stream.onNext("stop");
        testObserver.assertValues("create", "start", "resume", "pause");
        testObserver.assertNotComplete();
    }

    // Tests bindToLifecycle for any given RxActivityLifecycle implementation
    private void testBindToLifecycle(ActivityController<? extends LifecycleProvider<ActivityEvent>> controller) {
        LifecycleProvider<ActivityEvent> activity = controller.get();

        controller.create();
        TestObserver<Object> createObserver = stream.hide().compose(activity.bindToLifecycle()).test();
        stream.onNext("create");

        controller.start();
        TestObserver<Object> startObserver = stream.hide().compose(activity.bindToLifecycle()).test();
        stream.onNext("start");

        controller.resume();
        TestObserver<Object> resumeObserver = stream.hide().compose(activity.bindToLifecycle()).test();
        stream.onNext("resume");

        controller.pause();
        TestObserver<Object> pauseObserver = stream.hide().compose(activity.bindToLifecycle()).test();
        stream.onNext("pause");
        resumeObserver.assertNotComplete();
        resumeObserver.assertValues("resume");

        controller.stop();
        TestObserver<Object> stopObserver = stream.hide().compose(activity.bindToLifecycle()).test();
        stream.onNext("stop");
        startObserver.assertNotComplete();
        startObserver.assertValues("start", "resume", "pause");
        pauseObserver.assertNotComplete();
        pauseObserver.assertValues("pause");

        controller.destroy();
        stream.onNext("destroy");
        createObserver.assertNotComplete();
        createObserver.assertValues("create", "start", "resume", "pause", "stop");
        stopObserver.assertNotComplete();
        stopObserver.assertValues("stop");
    }

    // These classes are just for testing since components are abstract

    public static class TestRxActivity extends RxActivity {
    }

    public static class TestRxFragmentActivity extends RxFragmentActivity{
    }
}
