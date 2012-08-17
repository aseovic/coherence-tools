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


import com.tangosol.net.Member;


/**
 * An exception that is thrown by the <tt>Future.get</tt> method when the task
 * fails because of the executing member's departure from the cluster.
 *
 * @author Aleksandar Seovic  2009.11.03
*/
public class MemberLeftException
        extends RuntimeException
    {
    // ---- constructors ----------------------------------------------------

    public MemberLeftException(Member member)
        {
        m_member = member;
        }

    public MemberLeftException(Member member, String message)
        {
        super(message);
        m_member = member;
        }

    // ---- data members ----------------------------------------------------

    public Member getMember()
        {
        return m_member;
        }


    // ---- data members ----------------------------------------------------

    private Member m_member;
    }
