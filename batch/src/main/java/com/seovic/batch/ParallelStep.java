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


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


/**
 * @author Aleksandar Seovic  2009.11.04
 */
public class ParallelStep
        extends AbstractCompositeStep {

    public ParallelStep(String name) {
        super(name);
    }

    public void execute(ExecutionContext context) {
        List<Step> localSteps = new ArrayList<Step>();
        List<Step> remoteSteps = new ArrayList<Step>();
        for (Step step : getSteps()) {
            if (step instanceof CompositeStep) {
                localSteps.add(step);
            }
            else {
                remoteSteps.add(step);
            }
        }

        try {
            Thread localExecutor = null;
            Thread remoteExecutor = null;
            if (!localSteps.isEmpty()) {
                localExecutor = new Thread(new ParallelStepExecutor(
                        context.getLocalExecutor(), context, localSteps));
                localExecutor.start();
            }
            if (!remoteSteps.isEmpty()) {
                remoteExecutor = new Thread(new ParallelStepExecutor(
                        context.getRemoteExecutor(), context, remoteSteps));
                remoteExecutor.start();
            }

            if (localExecutor != null) {
                localExecutor.join();
            }
            if (remoteExecutor != null) {
                remoteExecutor.join();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ParallelStepExecutor
            implements Runnable {

        private final ExecutorService executor;
        private final ExecutionContext context;
        private final List<Step> steps;

        public ParallelStepExecutor(ExecutorService executor,
                                    ExecutionContext context,
                                    List<Step> steps) {
            this.executor = executor;
            this.context = context;
            this.steps = steps;
        }

        public void run() {
            try {
                List<Step> steps = this.steps;
                ExecutionContext context = this.context;

                List<CallableStepAdapter> callableSteps =
                        new ArrayList<CallableStepAdapter>(steps.size());
                for (Step step : steps) {
                    callableSteps.add(new CallableStepAdapter(step, context));
                }

                List<Future<StepResult>> results = executor.invokeAll(
                        callableSteps);
                for (int i = 0; i < results.size(); i++) {
                    Future<StepResult> result = results.get(i);
                    Step step = steps.get(i);
                    processStepResult(context, step, result);
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
