package com.babylon.orbit2.syntax.simple

import com.babylon.orbit2.syntax.Orbit2Dsl

/**
 * Represents the current context in which a simple orbit is executing.
 *
 * @property state The current state of the container
 */
@Orbit2Dsl
interface SimpleContext<STATE : Any> {
    val state: STATE
}
