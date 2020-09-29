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

package com.babylon.orbit2.syntax.simple

import com.babylon.orbit2.Container
import com.babylon.orbit2.ContainerHost
import com.babylon.orbit2.syntax.Orbit2Dsl
import com.babylon.orbit2.syntax.strict.OrbitDslPlugin

@Orbit2Dsl
class SimpleSyntax<S : Any, SE : Any>(internal val containerContext: OrbitDslPlugin.ContainerContext<S, SE>) {

    /**
     * The current state which can change throughout execution of the orbit block
     */
    val state: S get() = containerContext.state
}

/**
 * Reducers reduce the current state and incoming events to produce a new state.
 *
 * @param reducer the lambda reducing the current state and incoming event to produce a new state
 */
@Orbit2Dsl
suspend fun <S : Any, SE : Any> SimpleSyntax<S, SE>.reduce(reducer: suspend SimpleContext<S>.() -> S) {
    containerContext.apply {
        state = object : SimpleContext<S> {
            override val state: S
                get() = this@reduce.state
        }.reducer()
    }
}

/**
 * Side effects allow you to deal with things like tracking, navigation etc.
 *
 * There is also a special type of side effects - ones that are meant for the view to listen
 * to as one-off events that are awkward to represent as part of the state - typically things
 * like navigation, showing transient views like toasts etc.
 *
 * These are delivered through [Container.sideEffectFlow] by calling [SimpleSyntax.postSideEffect].
 *
 * @param sideEffect the side effect to post through the side effect flow
 */
@Orbit2Dsl
fun <S : Any, SE : Any> SimpleSyntax<S, SE>.postSideEffect(sideEffect: SE) {
    containerContext.postSideEffect(sideEffect)
}

/**
 * Build and execute an orbit flow on [Container].
 *
 * @param init lambda returning the operator chain that represents the flow
 */
@Orbit2Dsl
fun <STATE : Any, SIDE_EFFECT : Any> ContainerHost<STATE, SIDE_EFFECT>.orbit(init: suspend SimpleSyntax<STATE, SIDE_EFFECT>.() -> Unit) =
    container.orbit {
        SimpleSyntax(it).init()
    }
