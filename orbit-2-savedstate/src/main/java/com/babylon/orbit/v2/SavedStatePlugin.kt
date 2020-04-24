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

import androidx.lifecycle.SavedStateHandle

internal val Container.Companion.SAVED_STATE_KEY
    get() = "state"

fun <STATE : Any, SIDE_EFFECT : Any> Container.Companion.create(
    initialState: STATE,
    savedStateHandle: SavedStateHandle,
    settings: Container.Settings = Container.Settings(),
    onCreate: (() -> Unit)? = null
): Container<STATE, SIDE_EFFECT> {
    val savedState: STATE? = savedStateHandle[SAVED_STATE_KEY]

    val realContainer: Container<STATE, SIDE_EFFECT> =
        when {
            savedState != null -> create(savedState)
            onCreate != null -> create(initialState, settings, onCreate)
            else -> create(initialState, settings)
        }
    return SavedStateContainerDecorator(
        realContainer,
        savedStateHandle
    )
}

internal class SavedStateContainerDecorator<STATE : Any, SIDE_EFFECT : Any>(
    private val actual: Container<STATE, SIDE_EFFECT>,
    private val savedStateHandle: SavedStateHandle
) : Container<STATE, SIDE_EFFECT> {
    override val currentState: STATE
        get() = actual.currentState

    override val orbit: Stream<STATE>
        get() = object : Stream<STATE> {
            override fun observe(lambda: (STATE) -> Unit): Stream.Closeable {
                return orbit.observe {
                    savedStateHandle[Container.SAVED_STATE_KEY] = it
                    lambda(it)
                }
            }
        }

    override val sideEffect: Stream<SIDE_EFFECT>
        get() = actual.sideEffect

    override fun <EVENT : Any> orbit(
        event: EVENT,
        init: Builder<STATE, SIDE_EFFECT, EVENT>.() -> Builder<STATE, SIDE_EFFECT, *>
    ) = actual.orbit(event, init)
}