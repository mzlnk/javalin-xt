package io.mzlnk.javalin.xt.e2e

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class Application private constructor(private val process: Process) {

    private val _logs: Queue<String> = ConcurrentLinkedQueue()

    init {
        val scope = CoroutineScope(Dispatchers.Default)

        scope.launch {
            process.errorStream.bufferedReader().use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    _logs.add(line)
                }
            }
        }

        scope.launch {
            process.inputStream.bufferedReader().use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    _logs.add(line)
                }
            }
        }
    }

    val isStarted: Boolean get() = _logs.any { it.matches(APPLICATION_STARTED_REGEX) }
    val isRunning: Boolean get() = process.isAlive

    val assertionFailures: List<String>
        get() = _logs.filter { it.matches(ASSERTION_FAILURE_REGEX) }
            .map { ASSERTION_FAILURE_REGEX.matchEntire(it)?.groups?.get("error")?.value ?: "" }

    fun stop() {
        process.destroy()
    }

    fun printLogs() {
        println("-------------------------------------------------------------------------------------")
        println("Application logs:")
        println("-------------------------------------------------------------------------------------")
        _logs.forEach { println(it) }
        println("-------------------------------------------------------------------------------------")
    }

    companion object {

        private val APPLICATION_STARTED_REGEX = Regex(".*Javalin started in [0-9]+ms \\\\o/")
        private val ASSERTION_FAILURE_REGEX = Regex(".*java.lang.AssertionError: (?<error>.*)")

        fun create(process: Process): Application {
            return Application(process)
        }

    }


}