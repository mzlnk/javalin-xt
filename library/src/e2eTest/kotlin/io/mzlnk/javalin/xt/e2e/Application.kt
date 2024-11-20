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

    val loadedSingletons: Int get() = _logs.find { it.matches(LOADED_SINGLETONS_REGEX) }
        ?.let { LOADED_SINGLETONS_REGEX.matchEntire(it)?.groups?.get("count")?.value }
        ?.toInt()
        ?: throw IllegalStateException("No information about loaded singletons found")

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
        private val LOADED_SINGLETONS_REGEX = Regex(".*Loaded (?<count>[0-9]+) singletons")

        fun create(process: Process): Application {
            return Application(process)
        }

    }


}