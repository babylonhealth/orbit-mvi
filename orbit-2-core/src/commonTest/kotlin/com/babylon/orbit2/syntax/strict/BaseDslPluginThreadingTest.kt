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

package com.babylon.orbit2.syntax.strict

import com.babylon.orbit2.Container
import com.babylon.orbit2.ContainerHost
import com.babylon.orbit2.internal.CountDownLatch
import com.babylon.orbit2.internal.RealContainer
import com.babylon.orbit2.internal.runBlocking
import com.babylon.orbit2.test
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.newSingleThreadContext
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

@ExperimentalStdlibApi
internal class BaseDslPluginThreadingTest {

    companion object {
        const val ORBIT_THREAD_PREFIX = "orbit"
        const val BACKGROUND_THREAD_PREFIX = "IO"
    }

    @Test
    fun `reducer executes on orbit dispatcher`() {
        val action = Random.nextInt()
        val middleware = BaseDslMiddleware()
        val testFlowObserver = middleware.container.stateFlow.test()

        middleware.reducer(action)

        testFlowObserver.awaitCount(2)
        assertTrue {
            middleware.threadName.startsWith(ORBIT_THREAD_PREFIX)
        }
    }

    @Test
    fun `transformer executes on background dispatcher`() {
        val action = Random.nextInt()
        val middleware = BaseDslMiddleware()
        val testFlowObserver = middleware.container.stateFlow.test()

        middleware.transformer(action)

        testFlowObserver.awaitCount(2)
        assertTrue {
            middleware.threadName.startsWith(BACKGROUND_THREAD_PREFIX)
        }
    }

    @Test
    fun `posting side effects executes on orbit dispatcher`() {
        val action = Random.nextInt()
        val middleware = BaseDslMiddleware()
        val testFlowObserver = middleware.container.sideEffectFlow.test()

        middleware.postingSideEffect(action)

        testFlowObserver.awaitCount(1)
        assertTrue {
            middleware.threadName.startsWith(ORBIT_THREAD_PREFIX)
        }
    }

    @Test
    fun `side effect executes on orbit dispatcher`() {
        val action = Random.nextInt()
        val middleware = BaseDslMiddleware()

        middleware.sideEffect(action)

        runBlocking {
            middleware.latch.await()
        }

        assertTrue {
            middleware.threadName.startsWith(ORBIT_THREAD_PREFIX)
        }
    }

    private data class TestState(val id: Int)

    private class BaseDslMiddleware : ContainerHost<TestState, String> {

        @Suppress("EXPERIMENTAL_API_USAGE")
        override var container: Container<TestState, String> = RealContainer(
            initialState = TestState(42),
            parentScope = CoroutineScope(Dispatchers.Unconfined),
            settings = Container.Settings(
                orbitDispatcher = newSingleThreadContext(ORBIT_THREAD_PREFIX),
                backgroundDispatcher = newSingleThreadContext(BACKGROUND_THREAD_PREFIX)
            )
        )
        lateinit var threadName: String
        val latch = CountDownLatch(1)

        fun reducer(action: Int) = orbit {
            reduce {
                threadName = currentCoroutineContext()[CoroutineName]?.name.orEmpty()
                state.copy(id = action)
            }
        }

        fun transformer(action: Int) = orbit {
            transform {
                threadName = "currentCoroutineContext()[CoroutineName]?.name.orEmpty()"
                action + 5
            }
                .reduce {
                    state.copy(id = event)
                }
        }

        fun postingSideEffect(action: Int) = orbit {
            sideEffect {
                threadName = currentCoroutineContext()[CoroutineName]?.name.orEmpty()
                post(action.toString())
            }
        }

        fun sideEffect(action: Int) = orbit {
            sideEffect {
                threadName = currentCoroutineContext()[CoroutineName]?.name.orEmpty()
                latch.countDown()
                action.toString()
            }
        }
    }
}