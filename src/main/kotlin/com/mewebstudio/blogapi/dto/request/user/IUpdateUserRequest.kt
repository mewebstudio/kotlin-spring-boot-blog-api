package com.mewebstudio.blogapi.dto.request.user

interface IUpdateUserRequest {
    val firstname: String?
    val lastname: String?
    val gender: String?
    val email: String?
    val password: String?
    val passwordConfirm: String?
}
