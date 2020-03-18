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

data class State(val verified: Boolean = false)

interface BogusDependency {
    fun stub()
}

class MyClass(private val dependency: BogusDependency) :
    Host<State, Nothing> {
    override val container =
        Container.create<State, Nothing>(
            State()
        ) {
            created()
        }

    fun created() {
        dependency.stub()
        println("created!")
    }

    fun something(action: Boolean) = orbit(action) {
        transform {
            event.toString()
        }
            .transformRxJava2Observable {
                Observable.just(event, "true", "false", "true", "false")
            }
//            .transformSuspend {
//                delay(1000)
//                event
//            }
            .reduce {
                //println("${event::class}, $state")
                state.copy(verified = event.toBoolean())
            }
            .sideEffect {
                println("${event::class}, $state")
            }
            .sideEffect {
                somethingElse(event)
            }
    }

    fun somethingElse(action: String) = orbit(action) {
        sideEffect {
            println("something else $event")
        }
    }
}

fun main() {

    val middleware = MyClass(object :
        BogusDependency {
        override fun stub() {}
    })
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