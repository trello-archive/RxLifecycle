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

import android.app.Fragment;
import android.content.Intent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.FragmentTestUtil;
import rx.observers.TestSubscriber;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxFragmentLifecycleTest {

    @Test
    public void testRxEventFragment() {
        testActivityResult(new RxEventFragment());
    }

    @Test
    public void testRxEventDialogFragment() {
        testActivityResult(new RxEventDialogFragment());
    }

    private void testActivityResult(FragmentLifecycleEventProvider provider) {
        Fragment fragment = (Fragment) provider;
        FragmentTestUtil.startFragment(fragment);

        TestSubscriber<com.trello.rxlifecycle.events.ActivityResultEvent> testSubscriber = new TestSubscriber<>();
        provider.activityResult().subscribe(testSubscriber);

        int requestCode = 1;
        int resultCode = 2;
        Intent resultData = new Intent();
        resultData.putExtra("test", "test");
        com.trello.rxlifecycle.events.ActivityResultEvent activityResultEvent = ActivityResultEvent.create(requestCode, resultCode, resultData);

        fragment.onActivityResult(requestCode, resultCode, resultData);

        testSubscriber.assertValue(activityResultEvent);
    }
}
