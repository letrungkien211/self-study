package com.scalablescripts.auth.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Entity
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int = 0

    @Column
    var name = ""

    @Column(unique = true)
    var email = ""

    @Column
    var password = ""
        @JsonIgnore
        get
        set(value) {
            //field = value
            field =  BCryptPasswordEncoder().encode(value)
        }

    fun comparePassword(password: String) : Boolean {
        return BCryptPasswordEncoder().matches(password, this.password)
    }
}