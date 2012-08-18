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


/**
 * @author Aleksandar Seovic  2009.11.04
 */
public class StepResult
        implements Serializable {

    private final ExecutionContext context;
    private final ExecutionStatus status;
    private final Throwable exception;

    public StepResult(ExecutionContext context, ExecutionStatus status) {
        this(context, status, null);
    }

    public StepResult(ExecutionContext context, ExecutionStatus status,
                      Throwable exception) {
        this.context = context;
        this.status = status;
        this.exception = exception;
    }

    public ExecutionContext getContext() {
        return context;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public Throwable getException() {
        return exception;
    }
}
