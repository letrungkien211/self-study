package com.scalablescripts.auth

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class SmokeTest () {
    @Autowired
    private val authController: AuthController? = null

    @Test
    fun contextLoads() {
        Assertions.assertThat(authController).isNotNull
    }

}