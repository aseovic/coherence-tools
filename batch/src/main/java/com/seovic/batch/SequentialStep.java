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


import java.util.concurrent.Future;


/**
 * @author Aleksandar Seovic  2009.11.04
 */
public class SequentialStep
        extends AbstractCompositeStep {
    public SequentialStep(String name) {
        super(name);
    }

    public void execute(ExecutionContext context) {
        for (Step step : getSteps()) {
            try {
                Future<StepResult> result = step instanceof CompositeStep
                                            ? context.getLocalExecutor().submit(
                        new CallableStepAdapter(step, context))
                                            : context.getRemoteExecutor()
                                                    .submit(new CallableStepAdapter(
                                                            step, context));
                processStepResult(context, step, result);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
