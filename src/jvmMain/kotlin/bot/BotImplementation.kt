package bot

import CloseableCoroutineScope
import domain.Bot
import domain.models.Instruction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import utils.Logger
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class BotImplementation(
    appScope: CoroutineScope,
    private val onInstructionTimeout: (Exception) -> Unit
): Bot {

    private val botScope: CloseableCoroutineScope

    private val instructionList = InstructionList(::onListChanged)
    private var started = false
    private var working = false
    private var instructionExecutionJob: Job

    private val log = Logger(this.javaClass.name)

    init {
        botScope = CloseableCoroutineScope(Dispatchers.IO + appScope.coroutineContext + SupervisorJob())
        instructionExecutionJob = Job(botScope.coroutineContext.job)
        instructionExecutionJob.cancel()
    }

    override fun isAnyInstructionExecuting(): Result<Boolean> {
        return if (instructionList.list.isEmpty()) {
            Result.success(instructionExecutionJob.isActive)
        } else {
            Result.success(false)
        }
    }

    override fun isPause(): Result<Boolean> {
        return Result.success(started)
    }

    override fun add(instruction: Instruction): Result<Unit> {
        return Result.success(
            kotlin.run {
                instructionList.add(instruction)
            }
        )
    }

    override fun addAll(instructions: List<Instruction>): Result<Unit> {
        return Result.success(
            kotlin.run {
                instructionList.addAll(instructions)
            }
        )
    }

    override fun remove(instruction: Instruction): Result<Unit> {
        return Result.success(
            kotlin.run {
                instructionList.remove(instruction)
            }
        )
    }

    override fun remove(id: Int): Result<Unit> {
        return Result.success(
            kotlin.run {
                instructionList.remove(id)
            }
        )
    }

    override fun getInstructions(): Result<List<Instruction>> {
        return Result.success(instructionList.list)
    }

    override fun start(): Result<Unit> {
        return Result.success(
            kotlin.run {
                if (started.not()) {
                    started = true
                    setupNextInstruction()
                }
            }
        )
    }

    override fun stop(cancelInstructionExecution: Boolean): Result<Unit> {
        return Result.success(
            kotlin.run {
                started = false
                if (cancelInstructionExecution) cancelExecution()
            }
        )
    }

    override fun cancelExecution(): Result<Unit> {
        return Result.success(
            kotlin.run {
                if (instructionExecutionJob.isActive) {
                    instructionExecutionJob.cancel()
                    if (instructionList.isEmpty().not() && instructionList.first().working) {
                        instructionList.first().cancelExecution()
                    }
                }
            }
        )
    }

    private fun onListChanged(list: List<Instruction>) {
        log("onListChanged:")
        list.forEach { instruction ->
            log(
                "Instruction id - ${instruction.id} " +
                        "execution time - ${instruction.executionTime.toDate()} " +
                        "possible delay - ${instruction.possibleDelay} "
            )
        }
        setupNextInstruction()
    }

    private fun setupNextInstruction() {
        if (instructionList.isEmpty().not() && working.not() && started) {
            executeInstruction(instructionList.first())
        }
    }

    private fun executeInstruction(instruction: Instruction) {
        log("executeInstruction id ${instruction.id}")
        if (instructionExecutionJob.isActive) {
            instructionExecutionJob.cancel()
        }
        instructionExecutionJob = botScope.launch {
            log("instruction is executing id ${instruction.id}")
            delay(
                getExecutionTime(instruction)
            )
            working = true
            instruction.execute(
                ::onTimeout,
                ::onCanceled,
                ::onExecuted
            )
        }
    }

    private fun getExecutionTime(instruction: Instruction): Long {
        val currentTime = System.currentTimeMillis()
        val instructionExecutionTime =
            if (instruction.executionTime < instruction.executionTime + instruction.possibleDelay) {
                Random.nextLong(
                    instruction.executionTime,
                    instruction.executionTime + instruction.possibleDelay
                )
            } else {
                instruction.executionTime
            }
        return instructionExecutionTime - currentTime
    }

    private fun onTimeout(instruction: Instruction) {
        log("onTimeout id - ${instruction.id}")
        working = false
        started = false
        onInstructionTimeout(InstructionTimeoutException("Instruction timeout after waiting" +
                " ${instruction.timeout.toDate()}. Instruction description: ${instruction.description}"))
    }

    private fun onCanceled(instruction: Instruction) {
        log("onCancel id - ${instruction.id}")
        working = false
        setupNextInstruction()

    }

    private fun onExecuted(instruction: Instruction) {
        log("onExecuted id - ${instruction.id}")
        working = false
        instructionList.remove(instruction)
        instruction.onExecuted(instruction)
    }
}

private fun Long.toDate(): String {
    return SimpleDateFormat("HH:mm:ss.SSS").format(Date(this))
}
