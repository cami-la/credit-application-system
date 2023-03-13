package me.dio.credit.application.system.dto.response

import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.enummeration.Status
import java.math.BigDecimal
import java.util.*

data class CreditView(
  val creditCode: UUID,
  val creditValue: BigDecimal,
  val numberOfInstallment: Int,
  val status: Status,
  val emailCustomer: String?,
  val incomeCustomer: BigDecimal?
) {
  constructor(credit: Credit) : this(
    creditCode = credit.creditCode,
    creditValue = credit.creditValue,
    numberOfInstallment = credit.numberOfInstallments,
    status = credit.status,
    emailCustomer = credit.customer?.email,
    incomeCustomer = credit.customer?.income
  )
}
