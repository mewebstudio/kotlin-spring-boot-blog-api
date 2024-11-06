package com.mewebstudio.blogapi.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException : RuntimeException {
    constructor() : super("Bad request!")

    constructor(message: String) : super(message)
}
