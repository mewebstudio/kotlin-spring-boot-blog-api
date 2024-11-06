package com.mewebstudio.blogapi.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun <R : Any> R.logger(): Lazy<Logger> = lazy {
    LoggerFactory.getLogger((if (javaClass.kotlin.isCompanion) javaClass.enclosingClass else javaClass).name)
}
