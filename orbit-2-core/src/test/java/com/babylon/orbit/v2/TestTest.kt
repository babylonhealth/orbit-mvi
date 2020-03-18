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

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.atLeast
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestTest {
    @Test
    fun `newDSLTestTest`() {

        val mockDependency = mock<BogusDependency>()
        val testSubject = MyClass(mockDependency)

        whenever2(testSubject, State()) {
            something(true)
        }.test {
            states(
                { copy(verified = true) }
            )
            loopBack { somethingElse("true") }
        }
    }


    private fun <HOST : Host<STATE, SIDE_EFFECT>, STATE : Any, SIDE_EFFECT : Any>
            whenever2(testSubject: HOST, initialState: STATE, invocation: HOST.() -> Unit) =
        OrbitInvocation(
            spy(testSubject.apply { configureForTest(initialState) }),
            initialState,
            invocation
        )

    class OrbitInvocation<HOST : Host<STATE, SIDE_EFFECT>, STATE : Any, SIDE_EFFECT : Any>(
        private val host: HOST,
        private val initialState: STATE,
        private val invocation: HOST.() -> Unit
    ) {
        fun test(block: OrbitVerification<HOST, STATE, SIDE_EFFECT>.() -> Unit) {

            val orbitTestObserver = host.container.orbit.test()
            val sideEffectTestObserver = host.container.sideEffect.test()
            host.invocation()

            val verification = OrbitVerification(
                host,
                initialState
            )
                .apply(block)

            // sanity check the initial state
            assertEquals(initialState, orbitTestObserver.values.firstOrNull())

            assertStatesInOrder(
                orbitTestObserver.values.drop(1),
                verification.expectedStateChanges,
                initialState
            )

            Assertions.assertIterableEquals(
                verification.expectedSideEffects,
                sideEffectTestObserver.values
            )

            verify(host, atLeast(0)).orbit<Any>(any(), any())
            verify(host, atLeast(0)).container
            verify(host, atLeast(0)).invocation()

            verification.expectedLoopBacks.forEach {
                val f = it.invocation
                verify(host, times(it.times)).f()
            }

            verifyNoMoreInteractions(host)
        }
    }

    class OrbitVerification<HOST : Host<STATE, SIDE_EFFECT>, STATE : Any, SIDE_EFFECT : Any>(
        private val host: HOST,
        private val initialState: STATE
    ) {
        internal var expectedSideEffects = emptyList<SIDE_EFFECT>()
        internal var expectedStateChanges = emptyList<STATE.() -> STATE>()
        internal var expectedLoopBacks = mutableListOf<Times<HOST, STATE, SIDE_EFFECT>>()

        fun states(vararg expectedStateChanges: STATE.() -> STATE) {
            this.expectedStateChanges = expectedStateChanges.toList()
        }

        fun postedSideEffects(vararg expectedSideEffects: SIDE_EFFECT) {
            this.expectedSideEffects = expectedSideEffects.toList()
        }

        fun loopBack(times: Int = 1, block: HOST.() -> Unit) {
            this.expectedLoopBacks.add(Times(times, block))
        }

        data class Times<HOST : Host<STATE, SIDE_EFFECT>, STATE : Any, SIDE_EFFECT : Any>(
            val times: Int = 1,
            val invocation: HOST.() -> Unit
        )
    }
}

fun <T : Any> Stream<T>.test() = TestStreamObserver<T>(this)

class TestStreamObserver<T>(stream: Stream<T>) {
    private val _values = mutableListOf<T>()
    val values: List<T>
        get() = _values

    init {
        stream.observe {
            _values.add(it)
        }
    }
}


//fun whenever2()

/*
Things to verify:
1. Sequence of states
2. Sequence of side effects
3. "loopbacks"
4. Invocations on dependencies (mocks)
5. No other interactions
 */

data class State(val verified: Boolean = false)

interface BogusDependency {
    fun stub()
}

class MyClass(private val dependency: BogusDependency) : Host<State, Nothing> {
    override val container =
        Container.create<State, Nothing>(
            State()
        ) {
            created()
        }

    fun created() {
        dependency.stub()
        println("created!")
    }

    fun something(action: Boolean) = orbit(action) {
        transform {
            event.toString()

        }
//            .transformRxJava2Observable {
//                Observable.just(event, "true", "false", "true", "false")
//            }
//            .transformSuspend {
//                delay(1000)
//                event
//            }
            .reduce {
                //println("${event::class}, $state")
                state.copy(verified = event.toBoolean())
            }
            .sideEffect {
                print("${event::class}, $state")
            }
            .sideEffect {
                somethingElse(event)
            }
//            .sideEffect {
//                if(state.verified == true)
//                    something(false)
//            }
    }

    fun somethingElse(action: String) = orbit(action) {
        sideEffect {
            print("something else $event")
        }
    }

    private fun print(string: String) {
        println(string)
    }
}


fun <STATE : Any, SIDE_EFFECT : Any, T : Host<STATE, SIDE_EFFECT>> T.testSpy(initialState: STATE): T {
    val spy = spy(this)
    val container = TestContainer<STATE, SIDE_EFFECT>(initialState)
    doAnswer { container }.whenever(spy).container
    return spy
}

internal class TestContainer<STATE : Any, SIDE_EFFECT : Any>(
    initialState: STATE
) : RealContainer<STATE, SIDE_EFFECT>(initialState, Dispatchers.Unconfined) {
    private var dispatched = false

    override fun <EVENT : Any> orbit(
        event: EVENT,
        init: Builder<STATE, EVENT>.() -> Builder<STATE, *>
    ) {
        if (!dispatched) {
            dispatched = true
            runBlocking {
                collectFlow(
                    event,
                    init
                )
            }
        }
    }
}


/**
 * Helper function for asserting orbit state sequences. It applies the reductions specified in `nextState` in a cumulative way, based on
 * successive states.
 *
 * Usage:
 *
 * ```
 * testStateObserver.assertStateSequence(inOrder = true) {
 *     nextState { TestState() },
 *     nextState { previousState.copy(label = previousState.label + "baz") },
 *     nextState { previousState.copy(label = previousState.label + "bar") },
 *     nextState { previousState.copy(index = 3) }
 * }
 *```
 *
 * The first state provided cannot refer to the previous state because none exists at that point and will throw an exception if used.
 *
 * Fails assertions:
 *
 * - When more or less states have been emitted than expected
 * - In ordered mode (default), if an emitted state does not satisfy its corresponding expected assertion-reduction.
 * - In unordered mode, if an emitted state cannot be produced based on the previous using any of the assertion-reductions
 *
 * It is recommended to always use the ordered mode unless we cannot guarantee the order in which the states are emitted.
 *
 * Once an assertion-reduction is satisfied it is removed from further consideration.
 *
 * See `documentation/testing/testing-strategy/middleware-testing.md#assertions`.
 *
 * @param inOrder Whether the assertions should be evaluated in top-to-bottom order or in any order.
 * @param timeoutMillis How long to wait for the desired number of states to be emitted before continuing.
 */

private tailrec fun <T : Any> assertStatesInOrder(
    values: List<T>,
    assertions: List<T.() -> T>,
    previousState: T,
    satisfiedAssertions: Int = 0
) {
    when {
        values.isEmpty() && assertions.isEmpty() -> {
            /* Success! */
        }
        values.isEmpty() && satisfiedAssertions == 0 -> failNoStatesReceived(
            assertions,
            previousState
        )
        values.isNotEmpty() && assertions.isEmpty() -> failMoreStatesThanExpected(
            assertions,
            satisfiedAssertions,
            values
        )
        assertions.isNotEmpty() -> {
            val assertion = assertions.first()
            val expectedState = previousState?.assertion()

            if (expectedState == previousState) {
                // Assertion already satisfied by previous state, drop the assertion and continue the checks in case it was deduplicated by orbit
                println("Expected assertion at index $satisfiedAssertions is satisfied because the object is already in that state")

                assertStatesInOrder(
                    values,
                    assertions.drop(1),
                    previousState,
                    satisfiedAssertions + 1
                )
            } else {
                val actualState = values.firstOrNull()
                if (actualState == null) {
                    failLessStatesReceivedThanExpected(
                        assertions,
                        previousState,
                        satisfiedAssertions
                    )
                } else {
                    assertEquals(
                        expectedState,
                        actualState
                    ) { "Failed assertion at index $satisfiedAssertions:" }

                    assertStatesInOrder(
                        values.drop(1),
                        assertions.drop(1),
                        actualState,
                        satisfiedAssertions + 1
                    )
                }
            }
        }
    }
}

private fun <T : Any> failLessStatesReceivedThanExpected(
    assertions: List<T.() -> T>,
    previousState: T,
    satisfiedAssertions: Int
) {
    val expectedStates =
        assertions
            .fold(emptyList<T>()) { list, reducer ->
                list + (
                        list.lastOrNull() ?: previousState
                        ).reducer()
            }
    Assertions.fail<Unit>(
        "Failed assertions at indices ${satisfiedAssertions until (satisfiedAssertions + assertions.size)}, " +
                "expected states but never received:\n$expectedStates"
    )
}

private fun <T : Any> failNoStatesReceived(
    assertions: List<T.() -> T>,
    previousState: T
) {
    Assertions.fail<Unit> { "Expected ${assertions.size} states but none were emitted" }
}

private fun <T : Any> failMoreStatesThanExpected(
    assertions: List<T.() -> T>,
    satisfiedAssertions: Int,
    values: List<T>
) {
    // More states received than expected
    Assertions.fail<Unit>("Expected ${assertions.size + satisfiedAssertions} states but more were emitted:\n$values")
}

@DslMarker
annotation class AssertStateSequenceDsl

@AssertStateSequenceDsl
class AssertStateReceiver<T : Any>(
    private val list2: MutableList<StateReceiver<T>.() -> T>
) {
    fun nextState(func: StateReceiver<T>.() -> T) = list2.add(func)
}

@AssertStateSequenceDsl
interface StateReceiver<T : Any> {
    val previousState: T
}
