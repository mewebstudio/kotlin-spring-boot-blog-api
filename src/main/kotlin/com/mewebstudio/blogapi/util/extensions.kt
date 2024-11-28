package com.mewebstudio.blogapi.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun <R : Any> R.logger(): Lazy<Logger> = lazy {
    LoggerFactory.getLogger(if (this::class.isCompanion) this::class.java.enclosingClass else this::class.java)
}
