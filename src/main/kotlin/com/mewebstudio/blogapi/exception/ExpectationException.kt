package com.mewebstudio.blogapi.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
class ExpectationException : RuntimeException {
    constructor() : super("Expectation exception!")

    constructor(message: String) : super(message)
}
