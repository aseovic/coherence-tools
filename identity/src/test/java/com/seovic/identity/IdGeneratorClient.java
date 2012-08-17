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

package com.seovic.identity;


import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;


/**
 * Multi-threaded test client for {@link IdGenerator} implementations.
 *
 * @author Aleksandar Seovic  2008.11.24
 */
@SuppressWarnings({"unchecked", "EmptyCatchBlock"})
public class IdGeneratorClient {
    // ---- constructors ----------------------------------------------------

    /**
     * Construct IdGeneratorClient instance.
     *
     * @param generator identity generator to test
     */
    public IdGeneratorClient(IdGenerator generator) {
        this.generator = generator;
    }


    // ---- public methods --------------------------------------------------

    /**
     * Starts specified number of identity generation threads, each of which
     * should generate the specified number of identities.
     *
     * @param numThreads    number of identity generation threads to start
     * @param numIdentities number of identities to generate (per thread)
     *
     * @return a set of identities generated by all threads
     */
    public Set generateIdentities(int numThreads, int numIdentities) {
        Set identities = Collections.synchronizedSet(new HashSet());

        try {
            CountDownLatch start = new CountDownLatch(1);
            CountDownLatch done = new CountDownLatch(numThreads);

            for (int i = 0; i < numThreads; i++) {
                new IdentityGeneratorThread(identities, numIdentities, start,
                                            done).start();
            }

            start.countDown();
            done.await();
        }
        catch (InterruptedException ignore) {
        }

        return identities;
    }


    // ---- inner class: IdentityGeneratorThread ----------------------------

    /**
     * Generates specified number of identities and adds them to the result
     * set.
     */
    class IdentityGeneratorThread
            extends Thread {
        private final Set identities;
        private final int numIdentities;
        private final Random randomizer;
        private final CountDownLatch start;
        private final CountDownLatch done;

        public IdentityGeneratorThread(Set identities, int numIdentities,
                                       CountDownLatch start,
                                       CountDownLatch done) {
            this.identities = identities;
            this.numIdentities = numIdentities;
            this.randomizer = new Random();
            this.start = start;
            this.done = done;
        }

        public void run() {
            try {
                start.await();
                for (int i = 0; i < numIdentities; i++) {
                    identities.add(generator.generateId());
                    try {
                        Thread.sleep(randomizer.nextInt(10));
                    }
                    catch (InterruptedException e) {
                    }
                }
                done.countDown();
            }
            catch (InterruptedException ignore) {
            }
        }
    }


    // ---- data members ----------------------------------------------------

    /**
     * IdGenerator to test.
     */
    private IdGenerator generator;
}