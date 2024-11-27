package io.mzlnk.javalin.xt.utils

import org.junit.jupiter.params.provider.Arguments
import java.util.stream.Stream

fun testCases(vararg arguments: Arguments) = Stream.of(*arguments)

fun testCase(vararg parameters: Any) = Arguments.of(*parameters)