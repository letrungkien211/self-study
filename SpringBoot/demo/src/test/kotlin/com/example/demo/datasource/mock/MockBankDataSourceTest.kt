package com.example.demo.datasource.mock

import com.example.demo.model.Bank
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MockBankDataSourceTest {
    private val mockBankDataSource = MockBankDataSource()

    @Test
    fun `getBanks should provide a collection of banks`() {
        val banks = mockBankDataSource.retrieveBanks()

        assertThat(banks).hasSizeGreaterThan(2)
    }

    @Test
    fun `getBanks should provide valid bank accounts`() {
        val banks = mockBankDataSource.retrieveBanks()

        assertThat(banks).allMatch { it: Bank -> it.accountNumber.isNotBlank() }
        assertThat(banks).allMatch { it: Bank -> it.trust > 0 }
        assertThat(banks).allMatch { it: Bank -> it.transactionFee > 0 }

    }
}