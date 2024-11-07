package com.mewebstudio.blogapi.entity.specification.criteria

import com.mewebstudio.blogapi.util.Enums
import java.time.LocalDateTime

data class UserCriteria(
    var roles: List<Enums.RoleEnum>? = null,
    var genders: List<Enums.GenderEnum>? = null,
    var createdAtStart: LocalDateTime? = null,
    var createdAtEnd: LocalDateTime? = null,
    var updatedAtStart: LocalDateTime? = null,
    var updatedAtEnd: LocalDateTime? = null,
    var isBlocked: Boolean? = null,
    var q: String? = null
)
