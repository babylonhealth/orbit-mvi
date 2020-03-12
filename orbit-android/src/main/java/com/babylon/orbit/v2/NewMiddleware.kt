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

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlin.properties.Delegates

data class State(val verified: Boolean = false)

interface Stream<T> {
    fun observe(lambda: (T) -> Unit): Closeable

    interface Closeable {
        fun close()
    }
}

interface Host<STATE : Any, SIDE_EFFECT : Any> {
    val container: Container<STATE, SIDE_EFFECT>

    fun <EVENT : Any> orbit(event: EVENT, init: Builder<STATE, EVENT>.() -> Builder<STATE, *>) =
        container.orbit(event, init)
}

class Container<STATE : Any, SIDE_EFFECT : Any>(
    initialState: STATE
) {
    val currentState: STATE
        get() = stateChannel.value
    private val stateChannel = ConflatedBroadcastChannel(initialState)
    private val sideEffectChannel =
        BroadcastChannel<SIDE_EFFECT>(Channel.BUFFERED) // TODO sort out side effects and caching
    private val orbitDispatcher = Dispatchers.IO // TODO fix threading
    private val scope = CoroutineScope(orbitDispatcher) // TODO fix threading

    val orbit: Stream<STATE>
        get() = wrapFlow(stateChannel.asFlow().distinctUntilChanged().flowOn(orbitDispatcher))

    val sideEffect: Stream<SIDE_EFFECT>
        get() = wrapFlow(sideEffectChannel.asFlow().flowOn(orbitDispatcher))

    fun <EVENT : Any> orbit(event: EVENT, init: Builder<STATE, EVENT>.() -> Builder<STATE, *>) {
        runBlocking {
            Builder<STATE, EVENT>()
                .init().stack.fold(flowOf(event)) { flow: Flow<Any>, operator: Operator<STATE, *> ->
                    // TODO execute plugins
                    //val plugin: com.babylon.orbit.v2.OrbitPlugin
                    val plugin = BasePlugin<STATE>()
                    plugin.apply(
                        operator as Operator<STATE, Any>,
                        { Context(currentState, it) },
                        flow,
                        {
                            runBlocking(scope.coroutineContext) {
                                stateChannel.send(it())
                            }
                        } // TODO fix threading?
                    )
                }.launchIn(scope)
                .join()
        }
    }

    private fun <T> wrapFlow(flow: Flow<T>): Stream<T> {
        return object : Stream<T> {
            override fun observe(lambda: (T) -> Unit): Stream.Closeable {
                val job = flow
                    .onEach { lambda(it) }
                    .launchIn(scope) // Is this right?


                return object : Stream.Closeable {
                    override fun close() {
                        job.cancel()
                    }

                }
            }
        }
    }
}

class MyViewModel : ViewModel(), Host<State, Unit> {
    override val container = Container<State, Unit>(State())

    fun aFunc() = orbit(Unit) {
        transform {
            event
        }
            .transformSuspend {
                event.toString()
            }
            .transformFlow {
                flowOf(event, "true", "false", "true", "false")
            }
            .reduce {
                //println("${event::class}, $state")
                state.copy(verified = event.toBoolean())
            }
            .sideEffect {
                println("${event::class}, $state")
            }
    }
}

class MyClass : Host<State, Unit> {
    override val container = Container<State, Unit>(State())

    fun something(action: Boolean) = orbit(action) {
        transform {
            event.toString()
        }
            .transformFlow {
                flowOf(event, "true", "false", "true", "false")
            }
            .reduce {
                //println("${event::class}, $state")
                state.copy(verified = event.toBoolean())
            }
            .sideEffect {
                println("${event::class}, $state")
            }
    }
}

fun main() {

    val middleware = MyClass()
    middleware.container.orbit.observe {
        println("Stream: $it")
    }
//    middleware.container.orbit.asLiveData().observeForever {
//        println("LiveData: $it")
//    }
    val test = middleware.container.orbit.asRx().test()
    middleware.something(false)

    test.awaitCount(5)
    test.assertValues(State(false), State(true), State(false), State(true), State(false))

}