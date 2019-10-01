package com.babylon.orbit.sample

import com.babylon.orbit.ActionState
import com.babylon.orbit.LifecycleAction
import com.babylon.orbit.sample.domain.analytics.AnalyticsManager
import com.babylon.orbit.sample.domain.todo.GetTodoUseCase
import com.babylon.orbit.sample.presentation.TodoScreenAction
import com.babylon.orbit.sample.presentation.TodoScreenState
import com.babylon.orbit.sample.presentation.TodoScreenTransformer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Observable
import org.spekframework.spek2.Spek
import org.spekframework.spek2.lifecycle.CachingMode
import org.spekframework.spek2.style.gherkin.Feature

class TodoScreenTransformerSpek : Spek({

    val mockGetTodoUseCase by memoized(mode = CachingMode.EACH_GROUP) { mock<GetTodoUseCase>() }
    val mockAnalyticsManager by memoized(mode = CachingMode.EACH_GROUP) { mock<AnalyticsManager>() }
    val transformer by memoized(mode = CachingMode.EACH_GROUP) {
        TodoScreenTransformer(mockGetTodoUseCase, mock(), mock(), mockAnalyticsManager)
    }

    Feature("Todo transformers") {
        listOf(
                LifecycleAction.Created,
                TodoScreenAction.RetryAction
        ).forEach { event ->
            Scenario("load todo transformer is sent a $event") {

                Given("an $event") {}

                When("passing the event named ${event.javaClass} into the transformer") {
                    transformer.loadTodos(createActionState(TodoScreenState(), event)).test()
                }

                Then("should trigger the correct action") {
                    verify(mockGetTodoUseCase).getTodoList()
                }
            }
        }

        Scenario("a TodoScreenAction.TodoSelected event") {
            val todoId = 333
            lateinit var event: TodoScreenAction.TodoSelected

            Given("a todo selcted action") {
                event = TodoScreenAction.TodoSelected(todoId)
            }

            When("passing the event into the transformer") {
                transformer.trackSelectedTodo(createActionState(TodoScreenState(), event)).test()
            }

            Then("should trigger the correct action") {
                verify(mockAnalyticsManager).trackAnalytics(todoId.toString())
            }
        }
    }
})

private fun <ACTION : Any, STATE : Any> createActionState(action: ACTION, state: STATE) = Observable.just(
        ActionState(action, state)
)
