package com.mewebstudio.blogapi.entity.specification.criteria

import java.time.LocalDateTime
import java.util.UUID

data class CategoryCriteria(
    var users: List<UUID> = emptyList(),
    override var q: String? = null,
    override var createdAtStart: LocalDateTime? = null,
    override var createdAtEnd: LocalDateTime? = null,
    override var updatedAtStart: LocalDateTime? = null,
    override var updatedAtEnd: LocalDateTime? = null,
) : ICriteria
