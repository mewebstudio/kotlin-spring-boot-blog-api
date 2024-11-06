package com.mewebstudio.blogapi.util

import com.mewebstudio.blogapi.entity.specification.criteria.PaginationCriteria
import com.mewebstudio.blogapi.exception.BadRequestException
import org.slf4j.Logger
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

object PageRequestBuilder {
    private val log: Logger by logger()

    fun build(paginationCriteria: PaginationCriteria): PageRequest {
        if (paginationCriteria.page == null || paginationCriteria.page!! < 1) {
            log.warn("Page number is not valid")
            throw BadRequestException("Page must be greater than 0!")
        }

        paginationCriteria.page = paginationCriteria.page!! - 1

        if (paginationCriteria.size == null || paginationCriteria.size!! < 1) {
            log.warn("Page size is not valid")
            throw BadRequestException("Size must be greater than 0!")
        }

        var pageRequest = PageRequest.of(paginationCriteria.page!!, paginationCriteria.size!!)

        if (paginationCriteria.sortBy != null && paginationCriteria.sort != null) {
            val direction = getDirection(paginationCriteria.sort!!)

            val columnsList = paginationCriteria.columns?.toList() ?: emptyList()
            if (columnsList.contains(paginationCriteria.sortBy)) {
                pageRequest = pageRequest.withSort(Sort.by(direction, paginationCriteria.sortBy))
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
