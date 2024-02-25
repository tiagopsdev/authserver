package br.pucpr.authserver.users

import br.pucpr.authserver.exception.BadRequestException
import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.integration.dowloads.DownloadFile
import br.pucpr.authserver.integration.messaging.MessageClient
import br.pucpr.authserver.integration.quotes.Quote
import br.pucpr.authserver.integration.quotes.QuoteClient
import br.pucpr.authserver.roles.RoleRepository
import br.pucpr.authserver.security.Jwt
import br.pucpr.authserver.users.controller.responses.LoginResponse
import br.pucpr.authserver.users.controller.responses.UserResponse
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import kotlin.jvm.optionals.getOrNull
import kotlin.random.Random

@Service
class UserService(
    val repository: UserRepository,
    val roleRepository: RoleRepository,
    val avatarService: AvatarService,
    val jwt: Jwt,
    val quoteClient: QuoteClient,
    val messageClient: MessageClient,


) {
    fun insert(user: User): User {
        if (repository.findByEmail(user.email) != null) {
            throw BadRequestException("User already exists")
        }
        if (user.description.isBlank()){
            var quote: Quote? = null

            while (quote == null){
                quoteClient.randomQuote()?.also {
                    quote = if(it.text.length in 1..254) it else null

                }
                user.description = "'${quote!!.text}'  (${quote!!.author})"

            }

        }
        //user.avatar = avatarService.getAvatar(user)



        if (user.phone.isNotBlank()){

            val code = Random.nextInt(1000,10000)

            messageClient.sndSMS(user, "Hello ${user.name}! Here's yours AuthServer code. ${code}")

        }


        var userInserted = repository.save(user)
            .also { log.info("User inserted: {}", it.id) }

        userInserted.avatar = avatarService.getAvatar(userInserted)
        userInserted = repository.save(userInserted)

        return userInserted


    }

    fun update(id: Long, name: String): User? {
        val user = findByIdOrThrow(id)
        if (user.name == name) return null
        user.name = name
        return repository.save(user)
    }

    fun findAll(dir: SortDir = SortDir.ASC): List<User> = when (dir) {
        SortDir.ASC -> repository.findAll(Sort.by("name").ascending())
        SortDir.DESC -> repository.findAll(Sort.by("name").descending())
    }

    fun findByRole(role: String): List<User> = repository.findByRole(role)

    fun findByIdOrNull(id: Long) = repository.findById(id).getOrNull()
    private fun findByIdOrThrow(id: Long) =
        findByIdOrNull(id) ?: throw NotFoundException(id)

    fun delete(id: Long): Boolean {
        val user = findByIdOrNull(id) ?: return false
        if (user.roles.any { it.name == "ADMIN" }) {
            val count = repository.findByRole("ADMIN").size
            if (count == 1) throw BadRequestException("Cannot delete the last system admin!")
        }
        repository.delete(user)
        log.info("User deleted: {}", user.id)
        return true
    }

    fun addRole(id: Long, roleName: String): Boolean {
        val user = findByIdOrThrow(id)
        if (user.roles.any { it.name == roleName }) return false

        val role = roleRepository.findByName(roleName) ?:
            throw BadRequestException("Invalid role: $roleName")

        user.roles.add(role)
        repository.save(user)
        log.info("Granted role {} to user {}", role.name, user.id)
        return true
    }

    fun login(email: String, password: String): LoginResponse? {
        val user = repository.findByEmail(email) ?: return null
        if (user.password != password) return null

        log.info("User logged in. id={}, name={}", user.id, user.name)
        return LoginResponse(
            token = jwt.createToken(user),
            user = toResponse(user)
        )
    }

    fun saveAvatar(id: Long, avatar: MultipartFile){
        val user = findByIdOrThrow(id)
        user.avatar = avatarService.save(user,avatar)
        repository.save(user)
    }

    fun toResponse(user: User) =
        UserResponse(user, avatarService.urlFor(user.avatar))



    companion object {
        private val log = LoggerFactory.getLogger(UserService::class.java)
    }
}