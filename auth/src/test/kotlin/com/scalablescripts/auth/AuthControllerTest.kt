package com.scalablescripts.auth

import com.google.gson.Gson
import com.ninjasquad.springmockk.MockkBean
import com.scalablescripts.auth.dtos.LoginDTO
import com.scalablescripts.auth.dtos.RegisterDTO
import com.scalablescripts.auth.models.User
import com.scalablescripts.auth.services.UserService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest(@Autowired
                         private val mockMvc: MockMvc) {
    @MockkBean
    lateinit var userService: UserService
    private val gson = Gson()
    @Test
    fun `login with non-exist email`(){
        every {
            userService.findByEmail("not_exist@gmail.com")
        } returns null

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(LoginDTO("not_exist@gmail.com", ""))))
                .andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User not found."))
    }

    @Test
    fun `login with invalid credentials`() {
        val user = User()
        user.email = "email@gmail.com"
        user.password = "Notmatch"
        every {
            userService.findByEmail("email@gmail.com")
        } returns user

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(LoginDTO("email@gmail.com", "invalid password"))))
                .andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid password."))
    }

    @Test
    fun `login with valid credentials`(){
        val user = User()
        user.email = "email@gmail.com"
        user.password = "valid password"
        every{
            userService.findByEmail("email@gmail.com")
        } returns user

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(LoginDTO("email@gmail.com", "valid password"))))
                .andExpect(status().isOk)
    }
}