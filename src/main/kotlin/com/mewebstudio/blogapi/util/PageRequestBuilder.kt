package com.mewebstudio.blogapi.util

import com.mewebstudio.blogapi.exception.BadRequestException
import org.slf4j.Logger
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

object PageRequestBuilder {
    private val log: Logger by logger()

    /**
     * Builds a PageRequest based on provided pagination and sorting parameters.
     *
     * @param page Page number (default: null)
     * @param size Page size (default: null)
     * @param sortBy Column to sort by (default: null)
     * @param sort Sort direction, "asc" or "desc" (default: null)
     * @param columns Valid columns to sort by (default: null)
     * @return PageRequest instance
     * @throws BadRequestException If page or size is invalid
     */
    fun build(
        page: Int? = null,
        size: Int? = null,
        sortBy: String? = null,
        sort: String? = null,
        columns: Array<String>? = null
    ): PageRequest = run {
        if (page == null || page < 1) {
            log.warn("Page number is not valid")
            throw BadRequestException("Page must be greater than 0!")
        }

        if (size == null || size < 1) {
            log.warn("Page size is not valid")
            throw BadRequestException("Size must be greater than 0!")
        }

        var pageRequest = PageRequest.of(page - 1, size)

        if (sortBy != null && sort != null && columns?.contains(sortBy) == true) {
            val direction = getDirection(sort)
            pageRequest = pageRequest.withSort(Sort.by(direction, sortBy))
        }

        pageRequest
    }

    /**
     * Returns the Sort direction based on the provided string.
     *
     * @param sort "asc" or "desc" string
     * @return Sort.Direction ASC or DESC
     */
    private fun getDirection(sort: String): Sort.Direction {
        return if ("desc".equals(sort, ignoreCase = true)) {
            Sort.Direction.DESC
        } else {
            Sort.Direction.ASC
        }
    }
}
