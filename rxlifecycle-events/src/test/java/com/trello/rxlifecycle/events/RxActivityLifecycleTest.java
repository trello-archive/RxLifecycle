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

package com.trello.rxlifecycle.events;

import android.content.Intent;
import com.trello.rxlifecycle.events.support.RxEventFragmentActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;
import rx.observers.TestSubscriber;

import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxActivityLifecycleTest {

    @Test
    public void testRxEventActivity() {
        testActivityResult(Robolectric.buildActivity(RxEventActivity.class));
    }

    @Test
    public void testRxEventFragmentActivity() {
        testActivityResult(Robolectric.buildActivity(RxEventFragmentActivity.class));
    }

    @Test
    public void testRxAppCompatActivity() {
        // TODO: Doesn't work due to https://github.com/robolectric/robolectric/issues/1796
        //
        // testBindUntilEvent(Robolectric.buildActivity(RxAppCompatActivity.class));
        // testBindToLifecycle(Robolectric.buildActivity(RxAppCompatActivity.class));
    }

    private void testActivityResult(ActivityController<? extends ActivityLifecycleEventProvider> controller) {
        ActivityLifecycleEventProvider activity = controller.get();

        TestSubscriber<com.trello.rxlifecycle.events.ActivityResultEvent> testSubscriber = new TestSubscriber<>();
        activity.activityResult().subscribe(testSubscriber);

        Intent requestIntent = new Intent();
        int requestCode = 1;
        int resultCode = 2;
        Intent resultData = new Intent();
        resultData.putExtra("test", "test");
        com.trello.rxlifecycle.events.ActivityResultEvent activityResultEvent = ActivityResultEvent.create(requestCode, resultCode, resultData);

        shadowOf(controller.get()).startActivityForResult(requestIntent, requestCode);
        shadowOf(controller.get()).receiveResult(requestIntent, resultCode, resultData);

        testSubscriber.assertValue(activityResultEvent);
    }
}
