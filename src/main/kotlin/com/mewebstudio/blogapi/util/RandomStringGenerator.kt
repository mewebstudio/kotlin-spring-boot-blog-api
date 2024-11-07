package com.mewebstudio.blogapi.util

import kotlin.random.Random

class RandomStringGenerator(
    private val length: Int,
    private val symbols: String = ALPHA_NUM,
    private val random: Random = Random
) {
    companion object {
        private const val UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val DIGITS = "0123456789"
        private val LOWER = UPPER.lowercase()
        private val ALPHA_NUM = UPPER + LOWER + DIGITS
    }

    init {
        require(length > 0) { "Length must be greater than 0." }
        require(symbols.length >= 2) { "Symbols must contain at least two characters." }
    }

    /**
     * Generate random string.
     *
     * @return String
     */
    fun next(): String = (1..length).map { symbols[random.nextInt(symbols.length)] }.joinToString("")
}
