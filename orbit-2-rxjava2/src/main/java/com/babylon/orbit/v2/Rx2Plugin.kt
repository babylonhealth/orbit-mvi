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

import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.rx2.asFlow

internal class RxJavaObservable<S : Any, E : Any, E2 : Any>(val block: suspend Context<S, E>.() -> Observable<E2>) :
    Operator<S, E>

fun <S : Any, E : Any, E2 : Any> Builder<S, E>.transformRxJava2Observable(block: suspend Context<S, E>.() -> Observable<E2>): Builder<S, E2> {
    return Builder(
        stack + RxJavaObservable(
            block
        )
    )
}

internal class RxJava2Plugin<S : Any> : OrbitPlugin<S> {
    override fun <E : Any> apply(
        operator: Operator<S, E>,
        context: (event: E) -> Context<S, E>,
        flow: Flow<E>,
        setState: (suspend () -> S) -> Unit
    ): Flow<Any> {
        return if (operator is RxJavaObservable<*, *, *>) {
            flow.flatMapConcat {
                with(operator as RxJavaObservable<S, E, Any>) {
                    context(it).block()
                }.asFlow()
            }
        } else {
            flow
        }
    }
}

fun <T> Stream<T>.asRx() =
    Observable.create<T> { emitter ->
        val closeable = observe {
            if (!emitter.isDisposed) {
                emitter.onNext(it)
            }
        }
        emitter.setCancellable { closeable.close() }
    }