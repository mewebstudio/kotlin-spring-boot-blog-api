package com.mewebstudio.blogapi.entity.specification.criteria

import java.time.LocalDateTime

data class TagCriteria(
    override var q: String? = null,
    override var createdUsers: List<String>? = null,
    override var updatedUsers: List<String>? = null,
    override var createdAtStart: LocalDateTime? = null,
    override var createdAtEnd: LocalDateTime? = null,
    override var updatedAtStart: LocalDateTime? = null,
    override var updatedAtEnd: LocalDateTime? = null,
) : ICriteria
