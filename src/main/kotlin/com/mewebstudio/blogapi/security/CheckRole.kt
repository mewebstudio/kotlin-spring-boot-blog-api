package com.mewebstudio.blogapi.security

import com.mewebstudio.blogapi.util.Enums

/**
 * Check role for defining authorization.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CheckRole(
    val roles: Array<Enums.RoleEnum>
)
