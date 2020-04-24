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

import hu.akarnokd.kotlin.flow.PublishSubject
import hu.akarnokd.kotlin.flow.ReplaySubject
import hu.akarnokd.kotlin.flow.SubjectAPI
import hu.akarnokd.kotlin.flow.replay
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.Executors

@FlowPreview
open class RealContainer<STATE : Any, SIDE_EFFECT : Any>(
    initialState: STATE,
    settings: Container.Settings,
    orbitDispatcher: CoroutineDispatcher =
        Executors.newSingleThreadExecutor { Thread(it, "orbit") }.asCoroutineDispatcher(),
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO
) : Container<STATE, SIDE_EFFECT> {
    override val currentState: STATE
        get() = stateChannel.value
    private val stateChannel = ConflatedBroadcastChannel(initialState)
    private val sideEffectChannel: SubjectAPI<SIDE_EFFECT> =
        if (settings.sideEffectCaching) {
            ReplaySubject() // TODO this is wrong!! Will replay every side effect so far upon subscription
        } else {
            PublishSubject()
        }
    private val scope = CoroutineScope(orbitDispatcher)
    private val stateMutex = Mutex()
    private val sideEffectMutex = Mutex()

    override val orbit: Stream<STATE> =
        stateChannel.asFlow().distinctUntilChanged().replay(1) { it }.asStream(scope)

    override val sideEffect: Stream<SIDE_EFFECT> = sideEffectChannel.asStream(scope)

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
                                stateMutex.withLock {
                                    val reduced = it()
                                    stateChannel.send(reduced)
                                }
                            }.join()
                        },
                        { event: SIDE_EFFECT ->
                            scope.launch {
                                sideEffectMutex.withLock {
                                    sideEffectChannel.emit(event)
                                }
                            }
                        }
                    )
                }
            }.collect()
    }
}