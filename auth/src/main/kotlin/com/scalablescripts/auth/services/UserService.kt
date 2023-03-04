package com.scalablescripts.auth.services

import com.scalablescripts.auth.models.User
import com.scalablescripts.auth.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    fun save(user: User) : User {
        return userRepository.save(user)
    }

    fun findByEmail(email: String) : User? {
        return userRepository.findByEmail(email)
    }

    fun getById(id: Int) : User {
        return userRepository.getReferenceById(id)
    }
}