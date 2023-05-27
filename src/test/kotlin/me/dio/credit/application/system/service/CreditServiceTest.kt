package me.dio.credit.application.system.service

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.exception.BusinessException
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.service.impl.CreditService
import me.dio.credit.application.system.service.impl.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
class CreditServiceTest {
  @MockK
  lateinit var creditRepository: CreditRepository

  @MockK
  lateinit var customerService: CustomerService

  @InjectMockKs
  lateinit var creditService: CreditService

  @BeforeEach
  fun setUp() {
    MockKAnnotations.init(this)
    //creditService = CreditService(creditRepository, customerService)
  }

  @AfterEach
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun `should create credit `() {
    //given
    val credit: Credit = buildCredit()
    val customerId: Long = 1L

    every { customerService.findById(customerId) } returns credit.customer!!
    every { creditRepository.save(credit) } returns credit
    //when
    val actual: Credit = this.creditService.save(credit)
    //then
    verify(exactly = 1) { customerService.findById(customerId) }
    verify(exactly = 1) { creditRepository.save(credit) }

    Assertions.assertThat(actual).isNotNull
    Assertions.assertThat(actual).isSameAs(credit)
  }

  @Test
  fun `should not create credit when invalid day first installment`() {
    //given
    val invalidDayFirstInstallment: LocalDate = LocalDate.now().plusMonths(5)
    val credit: Credit = buildCredit(dayFirstInstallment = invalidDayFirstInstallment)

    every { creditRepository.save(credit) } answers { credit }
    //when
    Assertions.assertThatThrownBy { creditService.save(credit) }
      .isInstanceOf(BusinessException::class.java)
      .hasMessage("Invalid Date")
    //then
    verify(exactly = 0) { creditRepository.save(any()) }
  }

  @Test
  fun `should return list of credits for a customer`() {
    //given
    val customerId: Long = 1L
    val expectedCredits: List<Credit> = listOf(buildCredit(), buildCredit(), buildCredit())

    every { creditRepository.findAllByCustomerId(customerId) } returns expectedCredits
    //when
    val actual: List<Credit> = creditService.findAllByCustomer(customerId)
    //then
    Assertions.assertThat(actual).isNotNull
    Assertions.assertThat(actual).isNotEmpty
    Assertions.assertThat(actual).isSameAs(expectedCredits)

    verify(exactly = 1) { creditRepository.findAllByCustomerId(customerId) }
  }

  @Test
  fun `should return credit for a valid customer and credit code`() {
    //given
    val customerId: Long = 1L
    val creditCode: UUID = UUID.randomUUID()
    val credit: Credit = buildCredit(customer = Customer(id = customerId))

    every { creditRepository.findByCreditCode(creditCode) } returns credit
    //when
    val actual: Credit = creditService.findByCreditCode(customerId, creditCode)
    //then
    Assertions.assertThat(actual).isNotNull
    Assertions.assertThat(actual).isSameAs(credit)

    verify(exactly = 1) { creditRepository.findByCreditCode(creditCode) }
  }

  @Test
  fun `should throw BusinessException for invalid credit code`() {
    //given
    val customerId: Long = 1L
    val invalidCreditCode: UUID = UUID.randomUUID()

    every { creditRepository.findByCreditCode(invalidCreditCode) } returns null
    //when
    //then
    Assertions.assertThatThrownBy { creditService.findByCreditCode(customerId, invalidCreditCode) }
      .isInstanceOf(BusinessException::class.java)
      .hasMessage("Creditcode $invalidCreditCode not found")
    //then
    verify(exactly = 1) { creditRepository.findByCreditCode(invalidCreditCode) }
  }

  @Test
  fun `should throw IllegalArgumentException for different customer ID`() {
    //given
    val customerId: Long = 1L
    val creditCode: UUID = UUID.randomUUID()
    val credit: Credit = buildCredit(customer = Customer(id = 2L))

    every { creditRepository.findByCreditCode(creditCode) } returns credit
    //when
    //then
    Assertions.assertThatThrownBy { creditService.findByCreditCode(customerId, creditCode) }
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("Contact admin")

    verify { creditRepository.findByCreditCode(creditCode) }
  }

  companion object {
    private fun buildCredit(
      creditValue: BigDecimal = BigDecimal.valueOf(100.0),
      dayFirstInstallment: LocalDate = LocalDate.now().plusMonths(2L),
      numberOfInstallments: Int = 15,
      customer: Customer = CustomerServiceTest.buildCustomer()
    ): Credit = Credit(
      creditValue = creditValue,
      dayFirstInstallment = dayFirstInstallment,
      numberOfInstallments = numberOfInstallments,
      customer = customer
    )
  }
}