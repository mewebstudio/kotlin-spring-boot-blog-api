package com.mewebstudio.blogapi.entity.specification.criteria

import com.mewebstudio.blogapi.util.Enums
import java.time.LocalDateTime

data class UserCriteria(
    var roles: List<Enums.RoleEnum>? = null,
    var genders: List<Enums.GenderEnum>? = null,
    var isBlocked: Boolean? = null,
    override var q: String? = null,
    override var createdUsers: List<String>? = null,
    override var updatedUsers: List<String>? = null,
    override var createdAtStart: LocalDateTime? = null,
    override var createdAtEnd: LocalDateTime? = null,
    override var updatedAtStart: LocalDateTime? = null,
    override var updatedAtEnd: LocalDateTime? = null,
) : ICriteria
