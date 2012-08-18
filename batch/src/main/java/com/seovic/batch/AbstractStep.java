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
public abstract class AbstractStep
        implements Step, Serializable {

    private String name;
    private StepResult result;

    protected AbstractStep(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public StepResult getResult() {
        return result;
    }

    public void setResult(StepResult result) {
        this.result = result;
    }
}
