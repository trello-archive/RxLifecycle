package com.trello.rxlifecycle;

/**
 * Lifecycle events that can be emitted by Services.
 */
public enum ServiceEvent {

    CREATE,
    START_COMMAND,
    BIND,
    UNBIND,
    REBIND,
    DESTROY

}
