package com.example.demo.service

import com.example.demo.datasource.BankDataSource
import com.example.demo.model.Bank
import org.springframework.stereotype.Service

@Service
class BankService(private val dataSource: BankDataSource) {

    fun getBanks(): Collection<Bank> = dataSource.retrieveBanks()
    fun getBank(accountNumber: String) = dataSource.retrieveBank(accountNumber)
    fun createBank(bank: Bank) = dataSource.createBank(bank)
}