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

package com.seovic.core.concurrent;


import com.tangosol.net.AbstractInvocable;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.InvocationObserver;
import com.tangosol.net.InvocationService;
import com.tangosol.net.Member;
import com.tangosol.net.MemberEvent;
import com.tangosol.net.MemberListener;
import com.tangosol.util.Base;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.core.task.TaskExecutor;


/**
 * ExecutorService implementation that parallelizes task execution across the
 * cluster nodes using Invocation Service.
 *
 * @author Aleksandar Seovic  2009.11.02
 */
@SuppressWarnings({"unchecked"})
public class ClusteredExecutorService
        extends AbstractExecutorService
        implements TaskExecutor, Executor, MemberListener {
    // ---- data members ----------------------------------------------------

    /**
     * The name of the invocation service to use.
     */
    private final String invocationServiceName;

    /**
     * The invocation service to use.
     */
    private volatile InvocationService invocationService;

    /**
     * A set of members that can be used to execute tasks.
     */
    private volatile Set<Member> serviceMembers;

    /**
     * Member set iterator.
     */
    private volatile Iterator<Member> memberIterator;

    // ---- constructors and initializers -----------------------------------

    /**
     * Construct <tt>ClusteredExecutorService</tt> instance using default
     * invocation service ("InvocationService").
     */
    public ClusteredExecutorService() {
        this("InvocationService");
    }

    /**
     * Construct <tt>ClusteredExecutorService</tt> instance.
     *
     * @param invocationServiceName the name of the invocation service to use
     */
    public ClusteredExecutorService(String invocationServiceName) {
        this.invocationServiceName = invocationServiceName;
        initialize();
    }

    /**
     * Initialize this executor service.
     */
    protected synchronized void initialize() {
        String invocationServiceName = this.invocationServiceName;
        invocationService = (InvocationService)
                CacheFactory.getService(invocationServiceName);
        if (invocationService == null) {
            throw new IllegalArgumentException("Invocation service ["
                                               + invocationServiceName
                                               + "] is not defined.");
        }

        invocationService.addMemberListener(this);
        serviceMembers = invocationService.getInfo().getServiceMembers();
        memberIterator = serviceMembers.iterator();
    }


    // ---- Executor implementation -----------------------------------------

    /**
     * Executes the given command at some time in the future.
     *
     * @param command the runnable task
     */
    public void execute(Runnable command) {
        if (!(command instanceof ClusteredFutureTask)) {
            command = new ClusteredFutureTask<Object>(command, null);
        }
        command.run();
    }


    // ---- ExecutorService implementation ----------------------------------

    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new ClusteredFutureTask<T>(runnable, value);
    }

    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new ClusteredFutureTask<T>(callable);
    }

    public void shutdown() {
    }

    public List<Runnable> shutdownNow() {
        return null;
    }

    public boolean isShutdown() {
        return false;
    }

    public boolean isTerminated() {
        return false;
    }

    public boolean awaitTermination(long l, TimeUnit timeUnit)
            throws InterruptedException {
        return false;
    }


    // ---- MemberListener implementation -----------------------------------

    public void memberJoined(MemberEvent memberEvent) {
        addMember(memberEvent.getMember());
    }

    public void memberLeaving(MemberEvent memberEvent) {
        removeMember(memberEvent.getMember());
    }

    public void memberLeft(MemberEvent memberEvent) {
        removeMember(memberEvent.getMember());
    }


    // ---- helper methods --------------------------------------------------

    /**
     * Return the member that should execute submitted command.
     *
     * @return the member to execute submitted command on
     */
    protected synchronized Member getExecutionMember() {
        Iterator<Member> it = memberIterator;
        if (it == null || !it.hasNext()) {
            memberIterator = it = serviceMembers.iterator();
        }
        return it.next();
    }

    /**
     * Add member.
     *
     * @param member member to add
     */
    protected synchronized void addMember(Member member) {
        serviceMembers.add(member);
        memberIterator = serviceMembers.iterator();
    }

    /**
     * Remove member.
     *
     * @param member member to remove
     */
    protected synchronized void removeMember(Member member) {
        serviceMembers.remove(member);
        memberIterator = serviceMembers.iterator();
    }


    // ---- inner class: CallableAdapter ------------------------------------

    private static class CallableAdapter<T>
            implements Callable<T>, Serializable {

        private final Runnable runnable;
        private final T result;

        private CallableAdapter(Runnable runnable, T result) {
            this.runnable = runnable;
            this.result = result;
        }

        public T call()
                throws Exception {
            runnable.run();
            return result;
        }
    }


    // ---- inner class: InvocableAdapter -----------------------------------

    private static class InvocableAdapter<T>
            extends AbstractInvocable
            implements Serializable {

        private final Callable<T> callable;
        private volatile T result;

        public InvocableAdapter(Callable<T> callable) {
            this.callable = callable;
        }

        public void run() {
            try {
                result = callable.call();
            }
            catch (Exception e) {
                throw Base.ensureRuntimeException(e);
            }
        }

        public Object getResult() {
            return result;
        }
    }


    // ---- inner class: ClusteredFutureTask -----------------------------------

    private class ClusteredFutureTask<T>
            implements RunnableFuture<T>, InvocationObserver {
        // ---- data members --------------------------------------------

        private final Callable<T> callable;
        private final CountDownLatch latch;
        private volatile T result;
        private volatile Throwable exception;
        private volatile boolean fDone;

        // ---- constructors --------------------------------------------

        public ClusteredFutureTask(Callable<T> callable) {
            this.callable = callable;
            this.latch = new CountDownLatch(1);
        }

        public ClusteredFutureTask(Runnable runnable, T result) {
            this(new CallableAdapter<T>(runnable, result));
        }

        // ---- RunnableFuture implementation ---------------------------

        public void run() {
            invocationService.execute(
                    new InvocableAdapter(callable),
                    Collections.singleton(getExecutionMember()),
                    this);
        }

        public boolean cancel(boolean b) {
            return false;
        }

        public boolean isCancelled() {
            return false;
        }

        public boolean isDone() {
            return fDone;
        }

        public T get()
                throws InterruptedException, ExecutionException {
            latch.await();
            return getInternal();
        }

        public T get(long l, TimeUnit timeUnit)
                throws InterruptedException, ExecutionException,
                       TimeoutException {
            if (!latch.await(l, timeUnit)) {
                throw new TimeoutException();
            }

            return getInternal();
        }

        protected T getInternal()
                throws ExecutionException {
            if (exception != null) {
                throw new ExecutionException(exception);
            }
            return result;
        }

        // ---- InvocationObserver implementation -----------------------

        public void memberCompleted(Member member, Object result) {
            this.result = (T) result;
            fDone = true;
            latch.countDown();
        }

        public void memberFailed(Member member, Throwable throwable) {
            exception = throwable;
            fDone = true;
            latch.countDown();
        }

        public void memberLeft(Member member) {
            exception = new MemberLeftException(member);
            fDone = true;
            latch.countDown();
        }

        public void invocationCompleted() {
        }
    }
}
