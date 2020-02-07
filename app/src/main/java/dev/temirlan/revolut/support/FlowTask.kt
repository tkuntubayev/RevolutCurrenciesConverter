package dev.temirlan.revolut.support

import dev.temirlan.task.Task
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/6/20.
 */
class FlowTask<T>(
    private val id: String,
    private val taskStrategy: Task.Strategy,
    private val function: suspend () -> Flow<T>,
    private val onNext: (T) -> Unit,
    private val onError: (Throwable) -> Unit,
    private val scope: CoroutineScope = GlobalScope
) : Task {

    private var job: Job? = null

    override fun getId(): String {
        return id
    }

    @InternalCoroutinesApi
    override fun execute(onFinish: () -> Unit) {
        if (job == null) {
            job = scope.launch {
                try {
                    function().collect(object : FlowCollector<T> {
                        override suspend fun emit(value: T) {
                            withContext(Dispatchers.Main) { onNext(value) }
                        }
                    })
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) { onError(e) }
                }
            }
        }
    }

    override fun cancel() {
        job?.cancel()
    }

    override fun getStatus(): Task.Status {
        return when {
            job?.isCompleted == true -> Task.Status.Completed
            job?.isActive == true -> Task.Status.InProgress
            job?.isCancelled == true -> Task.Status.Cancelled
            else -> Task.Status.InProgress
        }
    }

    override fun getStrategy(): Task.Strategy {
        return taskStrategy
    }
}