/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seovic.coherence;


import com.seovic.pof.PortableTypeGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Goal which instruments portable types in order to implement serialization methods.
 *
 * @goal instrument
 * 
 * @phase process-classes
 */
public class PortableTypeMojo
    extends AbstractMojo
{
    /**
     * Location of the file.
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    public void execute()
        throws MojoExecutionException
    {
        try {
            PortableTypeGenerator.LOG = new PortableTypeGenerator.MavenLogger(getLog());
            PortableTypeGenerator.instrumentClasses(outputDirectory);
        }
        catch (IOException e) {
            throw new MojoExecutionException("Failed to instrument classes", e);
        }
    }
}
