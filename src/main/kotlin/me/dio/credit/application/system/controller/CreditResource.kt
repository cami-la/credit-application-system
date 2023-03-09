package me.dio.credit.application.system.controller

import me.dio.credit.application.system.dto.CreditDto
import me.dio.credit.application.system.dto.CreditView
import me.dio.credit.application.system.dto.CreditViewList
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.service.impl.CreditService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/credits")
class CreditResource(
  private val creditService: CreditService
) {

  @PostMapping
  fun saveCredit(@RequestBody creditDto: CreditDto): String {
    val credit: Credit = this.creditService.save(creditDto.toEntity())
    return "Credit ${credit.creditCode} - Customer ${credit.customer?.firstName} saved!"
  }

  @GetMapping
  fun findAllByCustomerId(@RequestParam(value = "customerId") customerId: Long) : List<CreditViewList> {
    return this.creditService.findAllByCustomer(customerId).stream()
      .map { credit: Credit -> CreditViewList(credit) }
      .collect(Collectors.toList())
  }

  @GetMapping
  fun findByCreditCode(@RequestParam(value = "customerId") customerId: Long,
                       @PathVariable creditCode: UUID) : CreditView {
    val credit: Credit = this.creditService.findByCreditCode(customerId, creditCode)
    return CreditView(credit)
  }

}