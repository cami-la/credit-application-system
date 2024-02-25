package me.dio.credit.application.system.repository

import me.dio.credit.application.system.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CustomerRepository: JpaRepository<Customer, Long> {
    @Query(value = "SELECT * FROM CUSTOMER WHERE CPF = ?1", nativeQuery = true)
    fun findByCustomerCPF(cpf: String) : Optional<Customer>
}