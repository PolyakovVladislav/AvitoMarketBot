package domain

import domain.models.Instruction

interface Bot {

    fun isAnyInstructionExecuting(): Result<Boolean>

    fun isPause(): Result<Boolean>

    fun add(instruction: Instruction): Result<Unit>

    fun addAll(instructions: List<Instruction>): Result<Unit>

    fun remove(instruction: Instruction): Result<Unit>

    fun remove(id: Int): Result<Unit>

    fun getInstructions(): Result<List<Instruction>>

    fun start(): Result<Unit>

    fun stop(cancelInstructionExecution: Boolean): Result<Unit>

    fun cancelExecution(): Result<Unit>
}