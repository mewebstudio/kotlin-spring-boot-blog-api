package com.mewebstudio.blogapi.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class TokenExpiredException : RuntimeException {
    constructor() : super("Token is expired!")

    constructor(message: String) : super(message)
}
