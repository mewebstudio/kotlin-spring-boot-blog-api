package com.mewebstudio.blogapi.entity.specification.criteria

import java.time.LocalDateTime

interface ICriteria {
    var q: String?
    var createdUsers: List<String>?
    var updatedUsers: List<String>?
    var createdAtStart: LocalDateTime?
    var createdAtEnd: LocalDateTime?
    var updatedAtStart: LocalDateTime?
    var updatedAtEnd: LocalDateTime?
}
