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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

open class RealContainer<STATE : Any, SIDE_EFFECT : Any>(
    initialState: STATE,
    private val orbitDispatcher: CoroutineDispatcher = Dispatchers.IO // TODO fix threading
) : Container<STATE, SIDE_EFFECT> {
    override val currentState: STATE
        get() = stateChannel.value
    private val stateChannel = ConflatedBroadcastChannel(initialState)
    private val sideEffectChannel =
        BroadcastChannel<SIDE_EFFECT>(Channel.BUFFERED) // TODO sort out side effects and caching
    private val scope = CoroutineScope(orbitDispatcher) // TODO fix threading

    override val orbit: Stream<STATE>
        get() = stateChannel.asFlow().distinctUntilChanged().flowOn(orbitDispatcher).asStream(scope)

    override val sideEffect: Stream<SIDE_EFFECT>
        get() = sideEffectChannel.asFlow().flowOn(orbitDispatcher).asStream(scope)

    override fun <EVENT : Any> orbit(
        event: EVENT,
        init: Builder<STATE, SIDE_EFFECT, EVENT>.() -> Builder<STATE, SIDE_EFFECT, *>
    ) {
        scope.launch {// TODO fix threading
            collectFlow(
                event,
                init
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun <EVENT : Any> collectFlow(
        event: EVENT,
        init: Builder<STATE, SIDE_EFFECT, EVENT>.() -> Builder<STATE, SIDE_EFFECT, *>
    ) {
        Builder<STATE, SIDE_EFFECT, EVENT>()
            .init().stack.fold(flowOf(event)) { flow: Flow<Any>, operator: Operator<STATE, *> ->
                Orbit.plugins.fold(flow) { flow2: Flow<Any>, plugin: OrbitPlugin ->
                    plugin.apply(
                        operator as Operator<STATE, Any>,
                        { RealContext(currentState, it) },
                        flow2,
                        {
                            runBlocking(scope.coroutineContext) {
                                stateChannel.send(it())
                            }
                        }, // TODO fix threading
                        { event : SIDE_EFFECT ->
                            sideEffectChannel.sendBlocking(event)
                        }
                    )
                }
            }.collect()
    }
}