package com.trello.rxlifecycle.components;

final class TestUtil {

    /**
     * Used for testing whether references are cleared.
     */
    static void cleanGarbage() {
        System.gc();
        try {
            Thread.sleep(100);
        }
        catch (Exception e) {
            // Ignore
        }
        System.gc();
    }

    private TestUtil() {
        throw new AssertionError("No instances!");
    }
}
