/*
 * Copyright 2009 Aleksandar Seovic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seovic.batch;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * Abstract base class for composite Step implementations.
 *
 * @author Aleksandar Seovic  2009.11.04
 */
public abstract class AbstractCompositeStep
        extends AbstractStep
        implements CompositeStep {

    private static final long serialVersionUID = 3517824007584120850L;

    // ---- data members ----------------------------------------------------

    /**
     * A list of child steps.
     */
    private List<Step> steps = new ArrayList<Step>();

    // ---- constructors ----------------------------------------------------

    protected AbstractCompositeStep(String name) {
        super(name);
    }


    // ---- CompositeStep implementation ------------------------------------

    public List<Step> getSteps() {
        return steps;
    }

    public CompositeStep addStep(Step step) {
        steps.add(step);
        return this;
    }


    // ---- helper methods --------------------------------------------------

    protected void processStepResult(ExecutionContext context,
                                     Step step,
                                     Future<StepResult> result)
            throws InterruptedException {
        try {
            StepResult stepResult = result.get();
            step.setResult(stepResult);
            context.merge(stepResult.getContext());
        }
        catch (ExecutionException e) {
            step.setResult(new StepResult(context, ExecutionStatus.FAILED, e));
        }
    }


    // ---- inner class: CallableStepAdapter --------------------------------

    public static class CallableStepAdapter
            implements Callable<StepResult>, Serializable {
        public CallableStepAdapter(Step step, ExecutionContext context) {
            m_step = step;
            m_context = context;
        }

        public StepResult call()
                throws Exception {
            ExecutionContext context = m_context;
            try {
                m_step.execute(context);
                return new StepResult(context, ExecutionStatus.COMPLETED);
            }
            catch (Throwable t) {
                return new StepResult(context, ExecutionStatus.FAILED, t);
            }
        }

        private final Step m_step;
        private final ExecutionContext m_context;
    }
}
