/*
 * Copyright 2020 Babylon Partners Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.babylon.orbit2.internal

import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.Continuation
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume

/**
 * Equivalent of [java.util.concurrent.CountDownLatch] for coroutines.
 */
@ExperimentalStdlibApi
interface CountDownLatch {
    /**
     * Decrements the count of the latch, resuming all suspended coroutines if
     * the count reaches zero.
     *
     * If the current count is greater than zero then it is decremented.
     * If the new count is zero then all suspended coroutines are resumed.
     *
     * If the current count equals zero then nothing happens.
     */
    fun countDown()

    /**
     * Returns the current count.
     *
     * This method is typically used for debugging and testing purposes.
     *
     * @return the current count
     */
    fun getCount(): Long

    /**
     * Causes the current coroutine to suspend until the latch has counted down to
     * zero, unless the couroutine is cancelled.
     *
     * If the current count is zero then this method returns immediately.
     *
     * If the current count is greater than zero then the current
     * coroutine is suspended and awaits until one of two things happen:
     *
     * * The count reaches zero due to invocations of the [countDown] method; or
     * * the coroutine is cancelled.
     *
     * If the current coroutine:
     *
     * * is already cancelled; or
     * * is cancelled while waiting,
     *
     * then [CancellationException] is thrown.
     *
     * @throws CancellationException if the current coroutine is cancelled
     *         while waiting or is already cancelled
     */
    @Throws(CancellationException::class)
    suspend fun await()

    /**
     * Causes the current coroutine to suspend until the latch has counted down to
     * zero, unless the couroutine is cancelled.
     *
     * If the current count is zero then this method returns immediately.
     *
     * If the current count is greater than zero then the current
     * coroutine is suspended and awaits until one of two things happen:
     *
     * * The count reaches zero due to invocations of the [countDown] method; or
     * * the coroutine is cancelled; or
     * * the specified waiting time elapses.
     *
     * If the current coroutine:
     *
     * * is already cancelled; or
     * * is cancelled while waiting,
     *
     * then [CancellationException] is thrown.
     *
     * If the specified waiting time elapses then the value `false`
     * is returned.  If the time is less than or equal to zero, the method
     * will not wait at all.
     *
     * @param timeout the maximum time to wait
     * @return `true` if the count reached zero and `false`
     *         if the waiting time elapsed before the count reached zero
     * @throws CancellationException if the current coroutine is cancelled
     *         while waiting or is already cancelled
     */
    @Throws(CancellationException::class)
    suspend fun await(time: Long): Boolean

    /**
     * Factory for [CountDownLatch] instances.
     */
    companion object {
        /**
         * Creates new [CountDownLatch] instance.
         *
         * @param initialCount initial count of the latch.
         */
        operator fun invoke(initialCount: Int): CountDownLatch = CountDownLatchImpl(initialCount)
    }
}

@ExperimentalStdlibApi
internal class CountDownLatchImpl(initialCount: Int) : CountDownLatch {
    private var count = initialCount

    private val continuations = mutableListOf<Continuation<Unit>>() // should probably use LockFreeLinkedListNode instead

    private val lock = reentrantLock()

    init {
        if (initialCount < 0) {
            throw IllegalArgumentException("initialCount < 0")
        }
    }

    override fun countDown() {
        val doResume = lock.withLock {
            count != 0 && (--count == 0)
        }

        if (doResume) {
            continuations.forEach {
                it.resume(Unit)
            }

            continuations.clear()
        }
    }

    override fun getCount() = lock.withLock { count.toLong() }

    override suspend fun await(time: Long): Boolean =
        withTimeoutOrNull(time) { await() } != null

    override suspend fun await() {
        var locked = true
        lock.lock()

        try {
            if (count > 0) {
                suspendCancellableCoroutine<Unit> { cont ->
                    continuations += cont
                    cont.invokeOnCancellation {
                        if (cont.isCancelled) {
                            lock.withLock {
                                continuations -= cont
                            }
                        }
                    }
                    lock.unlock()
                    locked = false
                }
            }
        } finally {
            if (locked) {
                lock.unlock()
            }
        }
    }
}