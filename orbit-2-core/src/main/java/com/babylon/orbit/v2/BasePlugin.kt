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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class Transform<S : Any, E : Any, E2 : Any>(val block: Context<S, E>.() -> E2) :
    Operator<S, E2>

internal class SideEffect<S : Any, E : Any>(val block: Context<S, E>.() -> Unit) :
    Operator<S, E>

internal class Reduce<S : Any, E : Any>(val block: Context<S, E>.() -> Any) :
    Operator<S, E>

fun <S : Any, E : Any, E2 : Any> Builder<S, E>.transform(block: Context<S, E>.() -> E2): Builder<S, E2> {
    return Builder(
        stack + Transform(
            block
        )
    )
}

fun <S : Any, E : Any> Builder<S, E>.sideEffect(block: Context<S, E>.() -> Unit): Builder<S, E> {
    return Builder(
        stack + SideEffect(
            block
        )
    )
}

fun <S : Any, E : Any> Builder<S, E>.reduce(block: Context<S, E>.() -> S): Builder<S, E> {
    return Builder(
        stack + Reduce(
            block
        )
    )
}

internal class BasePlugin<S : Any> : OrbitPlugin<S> {
    override fun <E : Any> apply(
        operator: Operator<S, E>,
        context: (event: E) -> Context<S, E>,
        flow: Flow<E>,
        setState: (suspend () -> S) -> Unit
    ): Flow<Any> {
        return when (operator) {
            is Transform<*, *, *> -> flow.map {
                @Suppress("UNCHECKED_CAST")
                with(operator as Transform<S, E, Any>) {
//                    withContext(Dispatchers.IO) {
                    context(it).block()
//                    }
                }
            }
            is SideEffect -> flow.onEach {
                with(operator) {
                    context(it).block()
                }
            }
            is Reduce -> flow.onEach {
                with(operator) {
                    // TODO line below blocking
                    setState { context(it).block() as S }
                }
            }
            else -> flow
        }
    }
}
