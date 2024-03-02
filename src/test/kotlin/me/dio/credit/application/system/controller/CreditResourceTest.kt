package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.dto.request.CreditDto
import me.dio.credit.application.system.dto.request.CustomerDto
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CreditRepository
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration
@AutoConfigureMockMvc
class CreditResourceTest {

    @Autowired
    private  lateinit var customerRepository: CustomerRepository
    @Autowired
    private lateinit var creditRepository: CreditRepository
    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/credits"
    }
    @BeforeEach
    fun setup() = creditRepository.deleteAll()

    @AfterEach
    fun tearDown() = creditRepository.deleteAll()

    @Test
    fun `should create a credit and return 201 status`() {
        val customer: Customer = customerRepository.save(builderCustomerDto().toEntity())
        val credit: CreditDto = builderCreditDto(customerId = customer.id)
        val valueString: String = objectMapper.writeValueAsString(credit)

        mockMvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valueString)
        ).andExpect(MockMvcResultMatchers.status().isCreated)


    }

    @Test
    fun `should not save a credit with custumer ID invalid and return 409 status`() {

        val credit: CreditDto = builderCreditDto(customerId = 1)
        val valueString: String = objectMapper.writeValueAsString(credit)

        mockMvc.perform(
                MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueString)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)

    }

    @Test
    fun `should find all credit and return 200 status`() {
        val customer: Customer = customerRepository.save(builderCustomerDto().toEntity())
        val credit: List<Credit> = listOf(
                creditRepository.save(builderCreditDto(customerId = customer.id).toEntity()),
                creditRepository.save(builderCreditDto(customerId = customer.id).toEntity())
        )


        mockMvc.perform(
                MockMvcRequestBuilders.get("$URL?customerId=${customer.id}")
                        .accept(MediaType.APPLICATION_JSON)

        ).andExpect(MockMvcResultMatchers.status().isOk)

    }

    @Test
    fun `should find credit by creditcode and return 200 status`() {
        val customer: Customer = customerRepository.save(builderCustomerDto().toEntity())
        val credit: Credit = creditRepository.save(builderCreditDto(customerId = customer.id).toEntity())

        mockMvc.perform(
                MockMvcRequestBuilders.get("$URL/${credit.creditCode}?customerId=${customer.id}")
                        .accept(MediaType.APPLICATION_JSON)

        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should not find credit with invalid creditcode and return 400 status`() {

        val invalidId = 2L

        mockMvc.perform(
                MockMvcRequestBuilders.get("$URL/$invalidId")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

    }

    private fun builderCreditDto(
            creditValue: BigDecimal = BigDecimal.valueOf(500.0),
            dayFirstInstallment: LocalDate = LocalDate.of(2024, Month.APRIL, 22),
            numberOfInstallments: Int = 5,
            customerId: Long?
    ): CreditDto = CreditDto(
            creditValue = creditValue,
            dayFirstOfInstallment = dayFirstInstallment,
            numberOfInstallments = numberOfInstallments,
            customerId = customerId!!
    )

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