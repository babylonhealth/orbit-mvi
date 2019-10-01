/*
 * Copyright 2019 Babylon Partners Limited
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

package com.babylon.orbit

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.containsOnly
import assertk.assertions.isEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import java.util.concurrent.CountDownLatch

internal class OrbitSpek : Spek({
    Feature("Orbit DSL") {

        Scenario("no flows") {
            lateinit var middleware: Middleware<State, String>
            lateinit var orbitContainer: BaseOrbitContainer<State, String>
            lateinit var emittedValues: List<State>

            Given("A middleware with no flows") {
                middleware = createTestMiddleware {}
                orbitContainer = BaseOrbitContainer(middleware)
            }

            When("connecting to the middleware") {
                emittedValues = orbitContainer.orbit.test().values()
            }

            Then("emits the initial state") {
                assertThat(emittedValues).containsExactly(middleware.initialState)
            }
        }

        Scenario("a flow that reduces an action") {
            val latch by memoized { CountDownLatch(1) }
            lateinit var middleware: Middleware<State, String>
            lateinit var orbitContainer: BaseOrbitContainer<State, String>
            lateinit var emittedValues: List<State>

            Given("A middleware with one reducer flow") {
                middleware = createTestMiddleware {
                    perform("something")
                            .on<Int>()
                            .withReducer { currentState, event ->
                                State(currentState.id + event).also { latch.countDown() }
                            }
                }
                orbitContainer = BaseOrbitContainer(middleware)
            }

            When("sending an action") {
                emittedValues = orbitContainer.orbit.test().values()
                orbitContainer.inputRelay.accept(5)
                latch.await()
            }

            Then("produces a correct end state") {
                assertThat(emittedValues).containsExactly(State(42), State(47))
            }
        }

        Scenario("a flow that reduces an action using a simple reducer") {
            val latch by memoized { CountDownLatch(1) }
            lateinit var middleware: Middleware<State, String>
            lateinit var orbitContainer: BaseOrbitContainer<State, String>
            lateinit var emittedValues: List<State>

            Given("A middleware with a simple reducer flow") {
                middleware = createTestMiddleware {
                    perform("something")
                            .on<Int>()
                            .withReducer { currentState ->
                                State(currentState.id + 22).also { latch.countDown() }
                            }
                }
                orbitContainer = BaseOrbitContainer(middleware)
            }

            When("sending an action") {
                emittedValues = orbitContainer.orbit.test().values()
                orbitContainer.inputRelay.accept(5)
                latch.await()
            }

            Then("produces a correct end state") {
                assertThat(emittedValues).containsExactly(State(42), State(64))
            }
        }

        Scenario("a flow with a transformer and reducer") {
            val latch = CountDownLatch(1)
            lateinit var middleware: Middleware<State, String>
            lateinit var orbitContainer: BaseOrbitContainer<State, String>
            lateinit var emittedValues: List<State>

            Given("A middleware with a transformer and reducer") {
                middleware = createTestMiddleware {
                    perform("something")
                            .on<Int>()
                            .transform { this.map { it.action * 2 } }
                            .withReducer { currentState, event ->
                                State(currentState.id + event).also { latch.countDown() }
                            }
                }
                orbitContainer = BaseOrbitContainer(middleware)
            }

            When("sending an action") {
                emittedValues = orbitContainer.orbit.test().values()
                orbitContainer.inputRelay.accept(5)
                latch.await()
            }

            Then("produces a correct end state") {
                assertThat(emittedValues).containsExactly(State(42), State(52))
            }
        }

        Scenario("a flow with a transformer and simple reducer") {
            val latch = CountDownLatch(1)
            lateinit var middleware: Middleware<State, String>
            lateinit var orbitContainer: BaseOrbitContainer<State, String>
            lateinit var emittedValues: List<State>

            Given("A middleware with a transformer and simple reducer") {
                middleware = createTestMiddleware {
                    perform("something")
                            .on<Int>()
                            .transform { this.map { it.action * 2 } }
                            .withReducer { currentState ->
                                State(currentState.id + 22).also { latch.countDown() }
                            }
                }
                orbitContainer = BaseOrbitContainer(middleware)
            }

            When("sending an action") {
                emittedValues = orbitContainer.orbit.test().values()
                orbitContainer.inputRelay.accept(5)
                latch.await()
            }

            Then("produces a correct end state") {
                assertThat(emittedValues).containsExactly(State(42), State(64))
            }
        }

        Scenario("a flow with two transformers and a reducer") {
            val latch = CountDownLatch(1)
            lateinit var middleware: Middleware<State, String>
            lateinit var orbitContainer: BaseOrbitContainer<State, String>
            lateinit var emittedValues: List<State>

            Given("A middleware with two transformers and a reducer") {
                middleware = createTestMiddleware {
                    perform("something")
                            .on<Int>()
                            .transform { this.map { it.action * 2 } }
                            .transform { this.map { it * 2 } }
                            .withReducer { currentState, event ->
                                State(currentState.id + event).also { latch.countDown() }
                            }
                }
                orbitContainer = BaseOrbitContainer(middleware)
            }

            When("sending an action") {
                emittedValues = orbitContainer.orbit.test().values()
                orbitContainer.inputRelay.accept(5)
                latch.await()
            }

            Then("produces a correct end state") {
                assertThat(emittedValues).containsExactly(State(42), State(62))
            }
        }

        Scenario("a flow with two transformers that is ignored") {
            val latch = CountDownLatch(1)
            lateinit var middleware: Middleware<State, String>
            lateinit var orbitContainer: BaseOrbitContainer<State, String>
            lateinit var emittedValues: List<State>

            Given("A middleware with two transformer flows") {
                middleware = createTestMiddleware {
                    perform("something")
                            .on<Int>()
                            .transform { this.map { it.action * 2 } }
                            .transform { this.map { it * 2 } }
                            .ignoringEvents()

                    perform("unlatch")
                            .on<Int>()
                            .transform {
                                latch.countDown()
                                this
                            }
                            .ignoringEvents()
                }
                orbitContainer = BaseOrbitContainer(middleware)
            }

            When("sending an action") {
                emittedValues = orbitContainer.orbit.test().values()
                orbitContainer.inputRelay.accept(5)
                latch.await()
            }

            Then("emits just the initial state after connecting") {
                assertThat(emittedValues).containsExactly(State(42))
            }
        }

        Scenario("a flow with a transformer loopback and a flow with a transformer and reducer") {
            data class IntModified(val value: Int)

            val latch = CountDownLatch(1)
            lateinit var middleware: Middleware<State, String>
            lateinit var orbitContainer: BaseOrbitContainer<State, String>
            lateinit var emittedValues: List<State>

            Given("A middleware with a transformer loopback flow and transform/reduce flow") {
                middleware = createTestMiddleware {
                    perform("something")
                            .on<Int>()
                            .transform { this.map { it.action * 2 } }
                            .loopBack { IntModified(it) }

                    perform("something")
                            .on<IntModified>()
                            .transform { this.map { it.action.value * 2 } }
                            .withReducer { currentState, event ->
                                State(currentState.id + event).also { latch.countDown() }
                            }
                }
                orbitContainer = BaseOrbitContainer(middleware)
            }

            When("sending an action") {
                emittedValues = orbitContainer.orbit.test().values()
                orbitContainer.inputRelay.accept(5)
                latch.await()
            }

            Then("produces a correct end state") {
                println(emittedValues)
                assertThat(emittedValues).containsExactly(State(42), State(62))
            }
        }

        Scenario("a flow with two transformers with reducers") {
            val latch = CountDownLatch(2)
            lateinit var middleware: Middleware<State, String>
            lateinit var orbitContainer: BaseOrbitContainer<State, String>
            lateinit var emittedValues: List<State>

            Given("A middleware with two transform/reduce flows") {
                middleware = createTestMiddleware {
                    perform("something")
                            .on<Int>()
                            .transform { this.map { it.action * 2 } }
                            .withReducer { _, event ->
                                State(event).also { latch.countDown() }
                            }

                    perform("something")
                            .on<Int>()
                            .transform { this.map { it.action + 2 } }
                            .withReducer { _, event ->
                                State(event).also { latch.countDown() }
                            }
                }
                orbitContainer = BaseOrbitContainer(middleware)
            }

            When("sending an action") {
                emittedValues = orbitContainer.orbit.test().values()
                orbitContainer.inputRelay.accept(5)
                latch.await()
            }

            Then("produces a correct series of states") {
                println(emittedValues)
                assertThat(emittedValues).containsOnly(State(42), State(10), State(7))
            }
        }
        Scenario("a flow with three transformers with reducers") {

            class One
            class Two
            class Three

            val latch = CountDownLatch(99)
            lateinit var middleware: Middleware<State, String>
            lateinit var orbitContainer: BaseOrbitContainer<State, String>
            lateinit var emittedValues: List<State>
            val expectedOutput = mutableListOf(State(0))

            Given("A middleware with three transform/reduce flows") {
                middleware = createTestMiddleware(State(0)) {
                    perform("one")
                            .on<One>()
                            .withReducer { _, _ ->
                                println("one ${Thread.currentThread().name}")
                                State(1).also { latch.countDown() }
                            }

                    perform("two")
                            .on<Two>()
                            .withReducer { _, _ ->
                                println("two ${Thread.currentThread().name}")
                                State(2).also { latch.countDown() }
                            }

                    perform("three")
                            .on<Three>()
                            .withReducer { _, _ ->
                                println("three ${Thread.currentThread().name}")
                                State(3).also { latch.countDown() }
                            }
                }
                orbitContainer = BaseOrbitContainer(middleware)
            }

            When("sending actions") {
                emittedValues = orbitContainer.orbit.test().values()
                for (i in 0 until 99) {
                    val value = (i % 3)
                    expectedOutput.add(State(value + 1))

                    orbitContainer.inputRelay.accept(
                            when (value) {
                                0 -> One()
                                1 -> Two()
                                2 -> Three()
                                else -> throw IllegalStateException("misconfigured test")
                            }
                    )
                }

                latch.await()
            }

            Then("produces a correct series of states") {
                println(emittedValues)
                println(expectedOutput)
                assertThat(emittedValues).isEqualTo(expectedOutput)
            }
        }

        Scenario("a flow with side effects") {

            val latch = CountDownLatch(1)
            lateinit var middleware: Middleware<State, String>
            lateinit var orbitContainer: BaseOrbitContainer<State, String>
            lateinit var emittedValues: List<State>
            lateinit var sideEffects: List<String>

            Given("A middleware with multiple side effects within one flow") {
                middleware = createTestMiddleware(State(1)) {
                    perform("something")
                            .on<Unit>()
                            .sideEffect { relay, actionState ->
                                relay.post(actionState.inputState.id.toString())
                            }
                            .transform {
                                map {
                                    it.inputState.id + 1
                                }
                            }
                            .sideEffect { relay, id ->
                                relay.post(id.toString())
                            }
                            .transform {
                                map {
                                    "three"
                                }
                            }
                            .sideEffect { relay, string ->
                                relay.post(string)
                                latch.countDown()
                            }
                            .ignoringEvents()
                }
                orbitContainer = BaseOrbitContainer(middleware)
            }

            When("sending actions") {
                emittedValues = orbitContainer.orbit.test().values()
                sideEffects = orbitContainer.sideEffect.test().values()

                orbitContainer.inputRelay.accept(Unit)
                latch.await()
            }

            Then("produces a correct series of states") {
                assertThat(emittedValues).containsExactly(State(1))
            }

            Then("produces a correct series of side effects") {
                assertThat(sideEffects).containsExactly("1", "2", "three")
            }
        }
    }
})

private fun createTestMiddleware(
    initialState: State = State(42),
    block: OrbitsBuilder<State, String>.() -> Unit
) = middleware<State, String>(initialState) {
    this.apply(block)
}

private data class State(val id: Int)
