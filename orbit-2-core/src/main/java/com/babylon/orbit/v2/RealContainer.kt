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

import hu.akarnokd.kotlin.flow.BehaviorSubject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.Executors

open class RealContainer<STATE : Any, SIDE_EFFECT : Any>(
    initialState: STATE,
    orbitDispatcher: CoroutineDispatcher = Executors.newSingleThreadExecutor()
        .asCoroutineDispatcher(),
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO
) : Container<STATE, SIDE_EFFECT> {
    override val currentState: STATE
        get() = runBlocking { stateChannel.first() }
    private val stateChannel = BehaviorSubject(initialState)

    //    override val currentState: STATE // TODO see if we can achieve this using vanilla channels
//        get() = stateChannel.value
//    private val stateChannel = ConflatedBroadcastChannel(initialState)
    private val sideEffectChannel =
        BroadcastChannel<SIDE_EFFECT>(Channel.BUFFERED) // TODO sort out side effects and caching
    private val scope = CoroutineScope(orbitDispatcher)
    private val mutex = Mutex()

    override val orbit: Stream<STATE> =
        stateChannel.distinctUntilChanged().asStream(scope)

    override val sideEffect: Stream<SIDE_EFFECT> =
        sideEffectChannel.asFlow().flowOn(Dispatchers.Unconfined).asStream(scope)

    override fun <EVENT : Any> orbit(
        event: EVENT,
        init: Builder<STATE, SIDE_EFFECT, EVENT>.() -> Builder<STATE, SIDE_EFFECT, *>
    ) {
        scope.launch {
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
                        backgroundDispatcher,
                        operator as Operator<STATE, Any>,
                        { RealContext(currentState, it) },
                        flow2,
                        {
                            scope.launch {
                                mutex.withLock {
                                    println(Thread.currentThread().name)
                                    val reduced = it()
                                    println(reduced)
                                    stateChannel.emit(reduced)
                                }
                            }.join()
                        },
                        { event: SIDE_EFFECT ->
                            sideEffectChannel.sendBlocking(event)
                        }
                    )
                }
            }.collect()
    }
}