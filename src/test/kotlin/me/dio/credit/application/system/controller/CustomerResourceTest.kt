package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.dto.request.CustomerDto
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerResourceTest {
  @Autowired private lateinit var customerRepository: CustomerRepository
  @Autowired private lateinit var mockMvc: MockMvc
  @Autowired private lateinit var objectMapper: ObjectMapper

  companion object {
    const val URL: String = "/api/customers"
  }

  @BeforeEach fun setup() = customerRepository.deleteAll()
  @AfterEach fun tearDown() = customerRepository.deleteAll()

  @Test
  fun `should create a customer and return 201 status`() {
    //given
    val customerDto: CustomerDto = builderCustomerDto()
    val valueAsString: String = objectMapper.writeValueAsString(customerDto)
    //when
    //then
    mockMvc.perform(MockMvcRequestBuilders.post(URL)
      .contentType(MediaType.APPLICATION_JSON)
      .content(valueAsString))
      .andExpect(MockMvcResultMatchers.status().isCreated)
      .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Cami"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Cavalcante"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("28475934625"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("camila@email.com"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("000000"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua da Cami, 123"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
      .andDo(MockMvcResultHandlers.print())
  }

  @Test
  fun `should not save a customer with same CPF and return 409 status`() {
    //given
    customerRepository.save(builderCustomerDto().toEntity())
    val customerDto: CustomerDto = builderCustomerDto()
    val valueAsString: String = objectMapper.writeValueAsString(customerDto)
    //when
    //then
    mockMvc.perform(MockMvcRequestBuilders.post(URL)
      .contentType(MediaType.APPLICATION_JSON)
      .content(valueAsString))
      .andExpect(MockMvcResultMatchers.status().isConflict)
      .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Conflict! Consult the documentation"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
      .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
      .andExpect(
        MockMvcResultMatchers.jsonPath("$.exception")
          .value("class org.springframework.dao.DataIntegrityViolationException")
      )
      .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
      .andDo(MockMvcResultHandlers.print())
  }

  @Test
  fun `should not save a customer with empty firstName and return 400 status`() {
    //given
    val customerDto: CustomerDto = builderCustomerDto(firstName = "")
    val valueAsString: String = objectMapper.writeValueAsString(customerDto)
    //when
    //then
    mockMvc.perform(MockMvcRequestBuilders.post(URL)
      .content(valueAsString)
      .contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isBadRequest)
      .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
      .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
      .andExpect(
        MockMvcResultMatchers.jsonPath("$.exception")
          .value("class org.springframework.web.bind.MethodArgumentNotValidException")
      )
      .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
      .andDo(MockMvcResultHandlers.print())
  }




  private fun builderCustomerDto(
    firstName: String = "Cami",
    lastName: String = "Cavalcante",
    cpf: String = "28475934625",
    email: String = "camila@email.com",
    income: BigDecimal = BigDecimal.valueOf(1000.0),
    password: String = "1234",
    zipCode: String = "000000",
    street: String = "Rua da Cami, 123",
  ) = CustomerDto(
    firstName = firstName,
    lastName = lastName,
    cpf = cpf,
    email = email,
    income = income,
    password = password,
    zipCode = zipCode,
    street = street
  )
}