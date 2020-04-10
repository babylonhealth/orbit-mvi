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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <T> Flow<T>.asStream(scope: CoroutineScope): Stream<T> {
    return object : Stream<T> {
        override fun observe(lambda: (T) -> Unit): Stream.Closeable {
            val job = this@asStream
                .onEach { lambda(it) }
                .launchIn(scope) // TODO Is this right?

            return object : Stream.Closeable {
                override fun close() {
                    job.cancel()
                }
            }
        }
    }
}