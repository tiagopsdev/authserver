package br.pucpr.authserver.users.controller.responses

import br.pucpr.authserver.users.User

data class UserResponse(
    val id: Long,
    val email: String,
    val name: String,
    val description: String,
    val avatar: String
) {
    constructor(user: User, avatarURL: String) :
            this(user.id!!, user.email, user.name, user.description, avatarURL)
}