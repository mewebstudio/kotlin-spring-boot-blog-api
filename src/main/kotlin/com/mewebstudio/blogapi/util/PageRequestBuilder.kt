package com.mewebstudio.blogapi.util

import com.mewebstudio.blogapi.exception.BadRequestException
import org.slf4j.Logger
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

object PageRequestBuilder {
    private val log: Logger by logger()

    fun build(
        page: Int? = null,
        size: Int? = null,
        sortBy: String? = null,
        sort: String? = null,
        columns: Array<String>? = null): PageRequest {
        if (page == null || page < 1) {
            log.warn("Page number is not valid")
            throw BadRequestException("Page must be greater than 0!")
        }

        if (size == null || size < 1) {
            log.warn("Page size is not valid")
            throw BadRequestException("Size must be greater than 0!")
        }

        var pageRequest = PageRequest.of(page - 1, size)

        if (sortBy != null && sort != null) {
            val direction = getDirection(sort)

            val columnsList = columns?.toList() ?: emptyList()
            if (columnsList.contains(sortBy)) {
                pageRequest = pageRequest.withSort(Sort.by(direction, sortBy))
            }
        }

        return pageRequest
    }

    /**
     * @param sort String
     * @return Sort.Direction
     */
    private fun getDirection(sort: String): Sort.Direction {
        return if ("desc".equals(sort, ignoreCase = true)) {
            Sort.Direction.DESC
        } else {
            Sort.Direction.ASC
        }
    }
}
