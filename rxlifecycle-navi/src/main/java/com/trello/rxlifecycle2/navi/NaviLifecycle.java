package com.trello.rxlifecycle2.navi;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.trello.navi2.NaviComponent;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import static com.trello.rxlifecycle2.internal.Preconditions.checkNotNull;

public final class NaviLifecycle {

    @NonNull
    @CheckResult
    public static LifecycleProvider<ActivityEvent> createActivityLifecycleProvider(@NonNull NaviComponent activity) {
        checkNotNull(activity, "activity == null");
        return new ActivityLifecycleProviderImpl(activity);
    }

    @NonNull
    @CheckResult
    public static LifecycleProvider<FragmentEvent> createFragmentLifecycleProvider(@NonNull NaviComponent fragment) {
        checkNotNull(fragment, "fragment == null");
        return new FragmentLifecycleProviderImpl(fragment);
    }

    private NaviLifecycle() {
        throw new AssertionError("No instances!");
    }
}
