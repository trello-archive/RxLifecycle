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

package com.trello.rxlifecycle.events.support;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.trello.rxlifecycle.events.ActivityResultEvent;
import com.trello.rxlifecycle.events.FragmentLifecycleEventProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import rx.observers.TestSubscriber;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxSupportFragmentLifecycleTest {

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
        startFragment(fragment);

        TestSubscriber<ActivityResultEvent> testSubscriber = new TestSubscriber<>();
        provider.activityResult().subscribe(testSubscriber);

        int requestCode = 1;
        int resultCode = 2;
        Intent resultData = new Intent();
        resultData.putExtra("test", "test");
        ActivityResultEvent activityResultEvent = ActivityResultEvent.create(requestCode, resultCode, resultData);

        fragment.onActivityResult(requestCode, resultCode, resultData);

        testSubscriber.assertValue(activityResultEvent);
    }

    // Easier than making everyone create their own shadows
    private void startFragment(Fragment fragment) {
        Robolectric.setupActivity(FragmentActivity.class).getSupportFragmentManager()
            .beginTransaction()
            .add(fragment, null)
            .commit();
    }
}
