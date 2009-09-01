package com.aptana.ide.core;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class MutexJobRule implements ISchedulingRule {

    private static MutexJobRule jobRule;

    public static MutexJobRule getInstance() {
        if (jobRule == null) {
            jobRule = new MutexJobRule();
        }
        return jobRule;
    }

    public boolean isConflicting(ISchedulingRule rule) {
        return rule == this;
    }

    public boolean contains(ISchedulingRule rule) {
        return rule == this;
    }
}
