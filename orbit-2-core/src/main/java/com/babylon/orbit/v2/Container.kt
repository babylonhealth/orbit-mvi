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

interface Container<STATE : Any, SIDE_EFFECT : Any> {
    val currentState: STATE
    val orbit: Stream<STATE>
    val sideEffect: Stream<SIDE_EFFECT>
    fun <EVENT : Any> orbit(event: EVENT, init: Builder<STATE, EVENT>.() -> Builder<STATE, *>)

    companion object {
        fun <STATE : Any, SIDE_EFFECT : Any> create(
            initialState: STATE
        ): Container<STATE, SIDE_EFFECT> =
            RealContainer(initialState)

        fun <STATE : Any, SIDE_EFFECT : Any> create(
            initialState: STATE,
            onCreate: () -> Unit
        ): Container<STATE, SIDE_EFFECT> =
            LazyCreateContainerDecorator(RealContainer(initialState), onCreate)
    }
}