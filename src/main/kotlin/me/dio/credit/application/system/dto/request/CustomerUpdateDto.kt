package me.dio.credit.application.system.dto.request

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import me.dio.credit.application.system.entity.Customer
import java.math.BigDecimal

data class CustomerUpdateDto(
  @field:NotEmpty(message = "Invalid input") val firstName: String,
  @field:NotEmpty(message = "Invalid input") val lastName: String,
  @field:NotNull(message = "Invalid input") val income: BigDecimal,
  @field:NotEmpty(message = "Invalid input") val zipCode: String,
  @field:NotEmpty(message = "Invalid input") val street: String
) {
  fun toEntity(customer: Customer): Customer {
    customer.firstName = this.firstName
    customer.lastName = this.lastName
    customer.income = this.income
    customer.address.street = this.street
    customer.address.zipCode = this.zipCode
    return customer
  }
}
