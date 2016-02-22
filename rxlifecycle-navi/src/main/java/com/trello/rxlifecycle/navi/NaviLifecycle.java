package com.trello.rxlifecycle.navi;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.trello.navi.NaviComponent;
import com.trello.rxlifecycle.ActivityLifecycleProvider;
import com.trello.rxlifecycle.FragmentLifecycleProvider;

import static com.trello.rxlifecycle.internal.Preconditions.checkNotNull;

public final class NaviLifecycle {

    @NonNull
    @CheckResult
    public static ActivityLifecycleProvider createActivityLifecycleProvider(@NonNull NaviComponent activity) {
        checkNotNull(activity, "activity == null");
        return new ActivityLifecycleProviderImpl(activity);
    }

    @NonNull
    @CheckResult
    public static FragmentLifecycleProvider createFragmentLifecycleProvider(@NonNull NaviComponent fragment) {
        checkNotNull(fragment, "fragment == null");
        return new FragmentLifecycleProviderImpl(fragment);
    }

    private NaviLifecycle() {
        throw new AssertionError("No instances!");
    }
}
