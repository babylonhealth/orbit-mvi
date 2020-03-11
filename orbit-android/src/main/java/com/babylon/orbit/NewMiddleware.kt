import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking


// TODO State here should probably be a lambda
data class Context<S : Any, E : Any>(val state: S, val event: E)

interface Operator<S : Any, E : Any>

internal class Transform<S : Any, E : Any, E2 : Any>(val block: suspend Context<S, E>.() -> E2) :
    Operator<S, E2>

internal class TransformFlow<S : Any, E : Any>(val block: suspend Context<S, E>.() -> Flow<Any>) :
    Operator<S, E>

internal class SideEffect<S : Any, E : Any>(val block: suspend Context<S, E>.() -> Unit) :
    Operator<S, E>

internal class Reduce<S : Any, E : Any>(val block: suspend Context<S, E>.() -> Any) : Operator<S, E>

class Builder<S : Any, E : Any>(val stack: List<Operator<S, *>> = emptyList()) {
    fun <E2 : Any> transform(block: suspend Context<S, E>.() -> E2): Builder<S, E2> {
        return Builder(stack + Transform(block))
    }

    fun <E2 : Any> transformFlow(block: suspend Context<S, E>.() -> Flow<E2>): Builder<S, E2> {
        return Builder(stack + TransformFlow(block))
    }

    fun sideEffect(block: suspend Context<S, E>.() -> Unit): Builder<S, E> {
        return Builder(stack + SideEffect(block))
    }

    fun reduce(block: suspend Context<S, E>.() -> S): Builder<S, E> {
        return Builder(stack + Reduce(block))
    }
}

internal class RxJavaObservable<S : Any, E : Any>(val block: suspend Context<S, E>.() -> Flow<Any>) :
    Operator<S, E>

fun <S : Any, E : Any, E2 : Any> Builder<S, E>.transformRxJava2Observable(block: suspend Context<S, E>.() -> Flow<E2>): Builder<S, E2> {
    return Builder(stack + RxJavaObservable(block))
}

internal class RxJava2Plugin<S : Any> : OrbitPlugin<S> {
    override fun <E : Any> apply(
        operator: Operator<S, E>,
        context: (event: E) -> Context<S, E>,
        flow: Flow<E>,
        setState: (suspend () -> S) -> Unit
    ): Flow<Any> {
        return if (operator is RxJavaObservable<S, E>) {
            flow.flatMapConcat {
                with(operator) {
                    context(it).block()
                }
            }
        } else {
            flow
        }
    }
}

interface OrbitPlugin<S : Any> {
    fun <E : Any> apply(
        operator: Operator<S, E>,
        context: (event: E) -> Context<S, E>,
        flow: Flow<E>,
        setState: (suspend () -> S) -> Unit
    ): Flow<Any>
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
                    context(it).block()
                }
            }
            is TransformFlow -> flow.flatMapConcat {
                with(operator) {
                    context(it).block()
                }
            }
            is SideEffect -> flow.onEach {
                with(operator) {
                    context(it).block()
                }
            }
            is Reduce -> flow.onEach {
                with(operator) {
                    // line below blocking
                    setState { context(it).block() as S }
                }
            }
            else -> flow
        }
    }
}

open class Orbit<S : Any>(initialState: S) {
    private var currentState: S = initialState

    fun <E : Any> orbit(event: E, init: Builder<S, E>.() -> Builder<S, *>) {
        runBlocking {
            Builder<S, E>().init().stack.fold(flowOf(event)) { flow: Flow<Any>, operator: Operator<S, *> ->
                // TODO execute plugins
                //val plugin: OrbitPlugin
                val plugin = BasePlugin<S>()
                plugin.apply(
                    operator as Operator<S, Any>,
                    { Context<S, Any>(currentState, it as Any) },
                    flow,
                    { currentState = runBlocking { it() } }
                )
            }.collect { }
        }
    }
}

data class State(val verified: Boolean = false)

class Middleware : Orbit<State>(State(false)) {
    fun aFunction(verify: Boolean) = orbit(verify) {
        transform {
            //println("${event::class}, $state")
            event.toString()
        }.transformFlow {
                flowOf(event, "true", "false", "true", "false")
            }.reduce {
                //println("${event::class}, $state")
                state.copy(verified = event.toBoolean())
            }
            /*.transform {
            println("${event::class}, $state")
            event.toBoolean()
        }.reduce {
            println("${event::class}, $state")
            state.copy(verified = true)
        }*/.sideEffect {
                println("${event::class}, $state")
            }
    }
}

fun main() {
    Middleware().aFunction(false)
}