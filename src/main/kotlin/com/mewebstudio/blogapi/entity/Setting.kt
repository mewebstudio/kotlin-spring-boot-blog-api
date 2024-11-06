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
data class Setting(
    @Column(name = "key", nullable = false)
    var key: String,

    @Column(name = "value", columnDefinition = "text")
    var value: String? = null
) : AbstractBaseEntity() {
    constructor() : this(key = "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Post) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return this::class.simpleName + "(id = $id, title = $key)"
    }
}
