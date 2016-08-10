package com.trello.rxlifecycle;

/**
 * Common interface for all RxFragment extensions.
 *
 * Useful if you are writing utilities on top of rxlifecycle-components
 * or implementing your own component not supported by this library.
 */
public interface FragmentLifecycleProvider extends LifecycleProvider<FragmentEvent> {
}
