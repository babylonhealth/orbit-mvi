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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

//fun <STATE : Any, SIDE_EFFECT : Any, T : Host<STATE, SIDE_EFFECT>> T.test(): T {
//    val field = this.javaClass.getDeclaredField("container")
//    field.isAccessible = true
//
//    field.set(
//        this,
//        TestContainer<STATE, SIDE_EFFECT>((field.get(this) as Container<STATE, SIDE_EFFECT>).currentState)
//    )
//    field.isAccessible = false
//    return this
//}

fun <STATE : Any, SIDE_EFFECT : Any, T : Host<STATE, SIDE_EFFECT>> T.configureForTest(initialState: STATE) {
    val field = this.javaClass.getDeclaredField("container")
    field.isAccessible = true

    field.set(
        this,
        TestContainer<STATE, SIDE_EFFECT>(initialState)
    )
    field.isAccessible = false
}

//fun <STATE : Any, SIDE_EFFECT : Any, T : Host<STATE, SIDE_EFFECT>> T.testSpy(initialState: STATE): T {
//    val spy = spy(this)
//    val container = TestContainer<STATE, SIDE_EFFECT>(initialState)
//    doAnswer { container }.whenever(spy).container
//    return spy
//}

internal class TestContainer<STATE : Any, SIDE_EFFECT : Any>(
    initialState: STATE
) : RealContainer<STATE, SIDE_EFFECT>(initialState, Dispatchers.Unconfined) {
    private var dispatched = false

    override fun <EVENT : Any> orbit(
        event: EVENT,
        init: Builder<STATE, EVENT>.() -> Builder<STATE, *>
    ) {
        if (!dispatched) {
            dispatched = true
            runBlocking {
                collectFlow(
                    event,
                    init
                )
            }
        }
    }
}