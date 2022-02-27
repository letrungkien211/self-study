package com.example.demo.controllers

import com.example.demo.model.Bank
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
internal class BankControllerTest @Autowired constructor(var mockMvc: MockMvc, var objectMapper: ObjectMapper) {

    @Nested
    @DisplayName("GET /api/banks")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetBanks {
        @Test
        fun `should return all banks`() {
            mockMvc.get("/api/banks").andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$[0].accountNumber") { value("1") }
                }
        }
    }

    @Nested
    @DisplayName("GET /api/banks/{accountNumber}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetBank {
        @Test
        fun `should return a bank`() {
            mockMvc.get("/api/banks/1").andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.accountNumber") { value("1") }
                }
        }

        @Test
        fun `should not return a bank if not exists`() {
            mockMvc.get("/api/banks/123").andDo { print() }
                .andExpect {
                    status { isNotFound() }
                }
        }
    }

    @Nested
    @DisplayName("POST /api/banks")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class CreateBank {
        @Test
        fun `should create a bank`() {
            val bank = Bank("22323", 22.2, 10)
            mockMvc.post("/api/banks") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(bank)
            }.andDo { print() }
                .andExpect {
                    status { isCreated() }
                    jsonPath("$.accountNumber") { value(bank.accountNumber) }
                }
        }

        @Test
        fun `should not create a bank if already exists`() {
            val bank = Bank("1", 22.2, 10)
            mockMvc.post("/api/banks") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(bank)
            }.andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                }
        }
    }
}