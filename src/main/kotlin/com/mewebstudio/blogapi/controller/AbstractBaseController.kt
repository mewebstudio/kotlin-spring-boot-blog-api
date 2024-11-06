package com.mewebstudio.blogapi.controller

import com.mewebstudio.blogapi.exception.BadRequestException
import com.mewebstudio.blogapi.service.MessageSourceService

abstract class AbstractBaseController {
    /**
     * Sort column check.
     *
     * @param messageSourceService MessageSourceService
     * @param sortColumns          String[]
     * @param sortBy               String
     */
    protected fun sortColumnCheck(
        messageSourceService: MessageSourceService,
        sortColumns: Array<String>,
        sortBy: String
    ) {
        if (!listOf(*sortColumns).contains(sortBy)) {
            throw BadRequestException(messageSourceService.get("invalid_sort_column"))
        }
    }
}
