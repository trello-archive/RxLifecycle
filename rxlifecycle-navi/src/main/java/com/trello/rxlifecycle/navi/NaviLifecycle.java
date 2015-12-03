package com.trello.rxlifecycle.navi;

import com.trello.navi.NaviComponent;
import com.trello.rxlifecycle.ActivityLifecycleProvider;
import com.trello.rxlifecycle.FragmentLifecycleProvider;

public final class NaviLifecycle {

    public static ActivityLifecycleProvider createActivityLifecycleProvider(NaviComponent activity) {
        return new ActivityLifecycleProviderImpl(activity);
    }

    public static FragmentLifecycleProvider createFragmentLifecycleProvider(NaviComponent fragment) {
        return new FragmentLifecycleProviderImpl(fragment);
    }

    private NaviLifecycle() {
        throw new AssertionError("No instances!");
    }
}
