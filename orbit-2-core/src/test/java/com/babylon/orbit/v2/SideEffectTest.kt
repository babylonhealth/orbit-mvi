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
import kotlinx.coroutines.Dispatchers
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class SideEffectTest {

    private val fixture = kotlinFixture()

    @DisplayName("Side effects are multicast to all current observers by default")
    @ParameterizedTest(name = "Caching is {0}")
    @ValueSource(booleans = [true, false])
    fun `side effects are multicast to all current observers by default`(caching: Boolean) {
        val action = fixture<Int>()
        val action2 = fixture<Int>()
        val action3 = fixture<Int>()
        val middleware = Middleware(caching)

        val testSideEffectObserver1 = middleware.container.sideEffect.test()
        val testSideEffectObserver2 = middleware.container.sideEffect.test()
        val testSideEffectObserver3 = middleware.container.sideEffect.test()

        middleware.someFlow(action)
        middleware.someFlow(action2)
        middleware.someFlow(action3)

        testSideEffectObserver1.awaitCount(3)
        testSideEffectObserver2.awaitCount(3)

        assertThat(testSideEffectObserver1.values).containsExactly(action, action2, action3)
        assertThat(testSideEffectObserver2.values).containsExactly(action, action2, action3)
        assertThat(testSideEffectObserver3.values).containsExactly(action, action2, action3)
    }

    @Test
    fun `when caching is turned on side effects are cached when there are no subscribers`() {
        val action = fixture<Int>()
        val action2 = fixture<Int>()
        val action3 = fixture<Int>()
        val middleware = Middleware(true)

        middleware.someFlow(action)
        middleware.someFlow(action2)
        middleware.someFlow(action3)

        val testSideEffectObserver1 = middleware.container.sideEffect.test()

        testSideEffectObserver1.awaitCount(3)

        assertThat(testSideEffectObserver1.values).containsExactly(action, action2, action3)
    }

    @Test
    fun `when caching is turned off side effects are not cached when there are no subscribers`() {
        val action = fixture<Int>()
        val action2 = fixture<Int>()
        val action3 = fixture<Int>()
        val middleware = Middleware(false)

        middleware.someFlow(action)
        middleware.someFlow(action2)
        middleware.someFlow(action3)

        Thread.sleep(10L) // TODO fix this

        val testSideEffectObserver1 = middleware.container.sideEffect.test()

        testSideEffectObserver1.awaitCount(3, 10L)

        assertThat(testSideEffectObserver1.values).isEmpty()
    }

    @Test
    fun `when caching is turned on only new side effects are emitted when resubscribing`() {
        val action = fixture<Int>()
        val action2 = fixture<Int>()
        val action3 = fixture<Int>()
        val middleware = Middleware(true)

        val testSideEffectObserver1 = middleware.container.sideEffect.test()

        middleware.someFlow(action)

        testSideEffectObserver1.awaitCount(1)
        testSideEffectObserver1.close()

        middleware.someFlow(action2)
        middleware.someFlow(action3)

        val testSideEffectObserver2 = middleware.container.sideEffect.test()
        testSideEffectObserver2.awaitCount(2)

        assertThat(testSideEffectObserver1.values).containsExactly(action)
        assertThat(testSideEffectObserver2.values).containsExactly(action2, action3)
    }

    private class Middleware(caching: Boolean) : Host<Unit, Int> {
        override val container = Container.create<Unit, Int>(
            Unit,
            Container.Settings(caching)
        )

        fun someFlow(action: Int) = orbit(action) {
            sideEffect {
                post(event)
            }
        }
    }
}
