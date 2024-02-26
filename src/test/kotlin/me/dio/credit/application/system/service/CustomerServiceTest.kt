package me.dio.credit.application.system.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.exception.BusinessException
import me.dio.credit.application.system.repository.CustomerRepository
import me.dio.credit.application.system.service.impl.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*

//@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CustomerServiceTest {
  @MockK lateinit var customerRepository: CustomerRepository
  @InjectMockKs lateinit var customerService: CustomerService

  @Test
  fun `should create customer`(){
    //given
    val fakeCustomer: Customer = buildCustomer()
    every { customerRepository.save(any()) } returns fakeCustomer
    //when
    val actual: Customer = customerService.save(fakeCustomer)
    //then
    Assertions.assertThat(actual).isNotNull
    Assertions.assertThat(actual).isSameAs(fakeCustomer)
    verify(exactly = 1) { customerRepository.save(fakeCustomer) }
  }

  @Test
  fun `should find customer by id`() {
    //given
    val fakeId: Long = Random().nextLong()
    val fakeCustomer: Customer = buildCustomer(id = fakeId)
    every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)
    //when
    val actual: Customer = customerService.findById(fakeId)
    //then
    Assertions.assertThat(actual).isNotNull
    Assertions.assertThat(actual).isExactlyInstanceOf(Customer::class.java)
    Assertions.assertThat(actual).isSameAs(fakeCustomer)
    verify(exactly = 1) { customerRepository.findById(fakeId) }
  }

  @Test
  fun `should not find customer by invalid id and throw BusinessException`() {
    //given
    val fakeId: Long = Random().nextLong()
    every { customerRepository.findById(fakeId) } returns Optional.empty()
    //when
    //then
    Assertions.assertThatExceptionOfType(BusinessException::class.java)
      .isThrownBy { customerService.findById(fakeId) }
      .withMessage("Id $fakeId not found")
    verify(exactly = 1) { customerRepository.findById(fakeId) }
  }

  @Test
  fun `should delete customer by id`() {
    //given
    val fakeId: Long = Random().nextLong()
    val fakeCustomer: Customer = buildCustomer(id = fakeId)
    every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)
    every { customerRepository.delete(fakeCustomer) } just runs
    //when
    customerService.delete(fakeId)
    //then
    verify(exactly = 1) { customerRepository.findById(fakeId) }
    verify(exactly = 1) { customerRepository.delete(fakeCustomer) }
  }


  companion object {
    fun buildCustomer(
      firstName: String = "Cami",
      lastName: String = "Cavalcante",
      cpf: String = "28475934625",
      email: String = "camila@gmail.com",
      password: String = "12345",
      zipCode: String = "12345",
      street: String = "Rua da Cami",
      income: BigDecimal = BigDecimal.valueOf(1000.0),
      id: Long = 1L
    ) = Customer(
      firstName = firstName,
      lastName = lastName,
      cpf = cpf,
      email = email,
      password = password,
      address = Address(
        zipCode = zipCode,
        street = street,
      ),
      income = income,
      id = id
    )
  }
}