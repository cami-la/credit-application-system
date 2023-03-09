package me.dio.credit.application.system.controller

import me.dio.credit.application.system.dto.CreditDto
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.service.impl.CreditService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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






}