package com.example.demo.datasource.mock

import com.example.demo.datasource.BankDataSource
import com.example.demo.model.Bank
import org.springframework.stereotype.Repository

@Repository
class MockBankDataSource : BankDataSource {
    private val banks = mutableListOf(Bank("1", 10.1, 3), Bank("2", 1.5, 1), Bank("3", 10.1, 3))
    override fun retrieveBanks(): Collection<Bank> = banks
    override fun retrieveBank(accountNumber: String): Bank {
        return banks.firstOrNull { it.accountNumber == accountNumber }
            ?: throw NoSuchElementException("AccountNumber=$accountNumber doesn't exist.")
    }

    override fun createBank(bank: Bank): Bank {
        if (banks.any { it.accountNumber == bank.accountNumber }) {
            throw IllegalArgumentException("AccountNumber=${bank.accountNumber} already exists.")
        }
        banks.add(bank)
        return bank
    }
}