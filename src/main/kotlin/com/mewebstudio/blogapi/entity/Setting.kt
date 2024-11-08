package com.mewebstudio.blogapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "settings",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["key"], name = "uk_settings_key")
    ]
)
class Setting(
    @Column(name = "key", nullable = false)
    var key: String? = null,

    @Column(name = "value", columnDefinition = "text")
    var value: String? = null
) : AbstractBaseEntity() {
    override fun toString(): String = this::class.simpleName + "(id = $id, key = $key)"
}
