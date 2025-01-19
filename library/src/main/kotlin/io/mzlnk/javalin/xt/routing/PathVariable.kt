package io.mzlnk.javalin.xt.routing

/**
 * Marks a parameter as a path variable.
 *
 * Path variables are extracted from the request path. Example:
 * /users/:id -> `id` is a path variable
 *
 * @param name name of path variable
 */
annotation class PathVariable(val name: String)
