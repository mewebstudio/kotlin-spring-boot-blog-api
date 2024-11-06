package com.mewebstudio.blogapi.util

class Enums {
    enum class RoleEnum(val value: String) {
        ADMIN("admin"),
        USER("user")
    }

    enum class GenderEnum(val value: String) {
        MALE("male"),
        FEMALE("female"),
        DIVERSE("diverse"),
        UNKNOWN("unknown")
    }
}
