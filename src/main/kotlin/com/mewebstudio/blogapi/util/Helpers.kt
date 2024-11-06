package com.mewebstudio.blogapi.util

import com.fasterxml.jackson.databind.ObjectMapper

class Helpers private constructor() {
    companion object {
        private val objectMapper = ObjectMapper()

        /**
         * Search for an enum by name -- optionally throw an exception if not found
         *
         * @param enumeration enum class
         * @param search      name of the enum
         * @param throwable   whether to throw an exception if not found
         * @param T          enum type
         * @return enum
         */
        fun <T : Enum<*>> searchEnum(enumeration: Class<T>, search: String, throwable: Boolean): T? {
            for (e in enumeration.enumConstants) {
                if (e.name.equals(search, ignoreCase = true)) {
                    return e
                }
            }

            if (throwable) {
                require(false) { "Invalid enum name: $search" }
            }

            return null
        }

        /**
         * Search for an enum by name -- do not throw an exception if not found
         *
         * @param enumeration enum class
         * @param search      name of the enum
         * @return enum
         */
        fun <T : Enum<*>> searchEnum(enumeration: Class<T>, search: String): T? {
            return searchEnum(enumeration, search, false)
        }

        /**
         * Parse JSON content
         *
         * @param content String
         * @return Object
         */
        fun parseJsonContent(content: String?): Any? {
            if (content.isNullOrBlank()) {
                return null
            }

            return if (content.startsWith("[")) {
                objectMapper.readValue(content, List::class.java)
            } else {
                objectMapper.readValue(content, Map::class.java)
            }
        }
    }
}
