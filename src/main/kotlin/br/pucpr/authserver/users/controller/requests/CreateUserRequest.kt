package br.pucpr.authserver.users.controller.requests

import br.pucpr.authserver.users.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CreateUserRequest(
    @field:Email
    val email: String?,
    @field:Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,}\$")
    val password: String?,
    @field:NotBlank
    val name: String?,
    val description: String?,
    val phone: String?
) {
    fun toUser() = User(
        email = email!!,
        password = password!!,
        name = name!!,
        description = description ?: "",
        phone = phone?.trim() ?: ""

    )
}