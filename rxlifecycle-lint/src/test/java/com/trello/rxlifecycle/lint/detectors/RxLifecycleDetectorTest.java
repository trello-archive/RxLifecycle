package com.trello.rxlifecycle.lint.detectors;

import java.util.Collections;
import java.util.List;

import com.android.tools.lint.detector.api.*;
import com.trello.rxlifecycle.lint.AbstractDetectorTest;

public class RxLifecycleDetectorTest extends AbstractDetectorTest {

    private static final String RX_FRAGMENT_CLASS = "bin/classes/RxFragment.class";
    @Override
    protected Detector getDetector() {
        return new RxLifecycleDetector();
    }

    @Override
    protected List<Issue> getIssues() {
        return Collections.singletonList(RxLifecycleDetector.ISSUE);
    }

    @Override
    protected String getTestResourceDirectory() {
        return "rx";
    }

    private String lintClassFile(String classFile) throws Exception {
        return lintFiles(".classpath", RX_FRAGMENT_CLASS, classFile);
    }

    /**
     * Test that a java file where a subscription is bound to lifecycle using RxFragment.
     */
    public void testRxLifecycleOk() throws Exception {
        assertEquals(NO_WARNINGS, lintClassFile("bin/classes/LifecycleOkKotlin.kt.class"));
    }

    /**
     * Test that a java file where a subscription is bound to lifecycle using Kotlin extension.
     */
    public void testRxLifecycleKotlinOk() throws Exception {
        assertEquals(NO_WARNINGS, lintClassFile("bin/classes/LifecycleOkKotlinExt.kt.class"));
    }

    /**
     * Test that a java file where a subscription is made in a class that is not a leak concern.
     */
    public void testUnconcernedParentIsNotError() throws Exception {
        assertEquals(NO_WARNINGS, lintClassFile("bin/classes/ExampleApi.class"));
    }

    /**
     * Test that a java file where a subscription is not bound using RxLifecycle
     */
    public void testRxLifecycleNotBound() throws Exception {
        String file = "bin/classes/LifecycleNotBoundKotlin.kt.class";
        String warningMessage = file
                + ": Error: "
                + RxLifecycleDetector.ISSUE.getBriefDescription(TextFormat.TEXT)
                + " [" + RxLifecycleDetector.ISSUE.getId() + "]\n"
                + "1 errors, 0 warnings\n";
        assertEquals(warningMessage, lintClassFile(file));

    }

}
