package domain.models

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.CoroutineContext

abstract class Instruction(
    val id: Int,
    var executionTime: Long,
    var possibleDelay: Long,
    val priority: Int,
    val timeout: Long,
    val onExecuted: (Instruction) -> Unit,
    val description: String
): CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO

    var working = false

    abstract suspend fun run()

    suspend fun execute(
        onTimeout: (Instruction) -> Unit,
        onCanceled: (Instruction) -> Unit,
        onExecuted: (Instruction) -> Unit
    ) {
        coroutineScope {
            launch {
                try {
                    withTimeout(timeout) {
                        working = true
                        run()
                        if (isActive) {
                            onExecuted(this@Instruction)
                        }
                    }
                } catch (e: TimeoutCancellationException) {
                    onTimeout(this@Instruction)
                } catch (e: CancellationException) {
                    onCanceled(this@Instruction)
                } finally {
                    working = false
                }
            }
        }
    }

    fun cancelExecution() {
        cancel()
    }
}