package com.trello.rxlifecycle.lint.detectors;

import java.util.*;

import com.android.annotations.NonNull;
import com.android.tools.lint.client.api.LintDriver;
import com.android.tools.lint.detector.api.*;
import org.objectweb.asm.tree.*;

/**
 * Lint check for RxLifecycle usage.
 */
public class RxLifecycleDetector extends Detector implements Detector.ClassScanner {

    private static final Class<? extends Detector> DETECTOR_CLASS = RxLifecycleDetector.class;
    private static final EnumSet<Scope>            DETECTOR_SCOPE = Scope.CLASS_FILE_SCOPE;

    private static final Implementation IMPLEMENTATION = new Implementation(
            DETECTOR_CLASS,
            DETECTOR_SCOPE
    );

    private static final String   ISSUE_ID          = "RxLifecycle";
    private static final String   ISSUE_DESCRIPTION = "RxJava subscription without binding to lifecycle.";
    private static final String   ISSUE_EXPLANATION = "A subscription can cause leaks if not un-subscribed when no longer needed.";
    private static final Category ISSUE_CATEGORY    = Category.CORRECTNESS;
    private static final int      ISSUE_PRIORITY    = 9;
    private static final Severity ISSUE_SEVERITY    = Severity.ERROR;

    public static final Issue ISSUE = Issue.create(
            ISSUE_ID,
            ISSUE_DESCRIPTION,
            ISSUE_EXPLANATION,
            ISSUE_CATEGORY,
            ISSUE_PRIORITY,
            ISSUE_SEVERITY,
            IMPLEMENTATION
    );

    private static final List<String> CONCERNCED_PARENTS = Arrays.asList(
            "android/app",
            "android/view",
            "android/widget",
            "android/support/v4/app",
            "android/support/v4/view",
            "android/support/v4/widget",
            "android/support/v7/app",
            "android/support/v7/view",
            "android/support/v7/widget",
            "android/support/design/widget"
    );

    private static final List<String> COMPOSE_METHODS = Arrays.asList(
            "bindToLifecycle",
            "bindUntilEvent",
            "bindActivity",
            "bindFragment",
            "bind",
            "bindUntilActivityEvent", // deprecated 0.5.0
            "bindUntilFragmentEvent", // deprecated 0.5.0
            "bindView" // deprecated 0.5.0
    );

    public RxLifecycleDetector() { }

    @Override public List<String> getApplicableCallNames() {
        return Collections.singletonList("subscribe");
    }

    @Override
    public void checkCall(@NonNull ClassContext context,
                          @NonNull ClassNode classNode,
                          @NonNull MethodNode method,
                          @NonNull MethodInsnNode call) {
        if (isConcernedParent(context.getDriver(), classNode)) {
            boolean bound = checkPrevious(call.getPrevious());
            if (!bound) {
                context.report(ISSUE, context.getLocation(call), ISSUE_DESCRIPTION);
            }
        }
    }

    private static boolean checkPrevious(AbstractInsnNode previous) {
        if (previous instanceof MethodInsnNode) {
            if (COMPOSE_METHODS.contains(((MethodInsnNode) previous).name)) {
                return true;
            }
        }
        return previous.getPrevious() != null && checkPrevious(previous.getPrevious());
    }

    private static boolean isConcernedParent(LintDriver driver, ClassNode classNode) {
        String owner = classNode.superName;
        while (owner != null) {
            for (String parent : CONCERNCED_PARENTS) {
                if (owner.startsWith(parent)) {
                    return true;
                }
            }
            owner = driver.getSuperClass(owner);
        }
        return false;
    }
}
