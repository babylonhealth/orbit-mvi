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

package com.babylon.orbit.v2

import com.appmattus.kotlinfixture.kotlinFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class RealContainerTest {

    private val fixture = kotlinFixture()

    @Test
    fun `initial state is emitted on connection`() {
        val initialState = fixture<TestState>()
        val middleware = Middleware(initialState)

        val testStateObserver = middleware.container.orbit.test()

        testStateObserver.awaitCount(1)

        assertThat(testStateObserver.values).containsExactly(initialState)
    }

    @Test
    fun `latest state is emitted on connection`() {
        val initialState = fixture<TestState>()
        val middleware = Middleware(initialState)
        val testStateObserver = middleware.container.orbit.test()
        val action = fixture<Int>()
        middleware.something(action)
        testStateObserver.awaitCount(1) // block until the state is updated

        val testStateObserver2 = middleware.container.orbit.test()
        testStateObserver.awaitCount(2)
        testStateObserver2.awaitCount(1)

        assertThat(testStateObserver.values).containsExactly(initialState, TestState(action))
        assertThat(testStateObserver2.values).containsExactly(TestState(action))
    }

    private data class TestState(val id: Int)

    private class Middleware(initialState: TestState) : Host<TestState, String> {
        override val container = Container.create<TestState, String>(initialState)

        fun something(action: Int) = orbit(action) {
            reduce {
                state.copy(id = event)
            }
        }
    }
}