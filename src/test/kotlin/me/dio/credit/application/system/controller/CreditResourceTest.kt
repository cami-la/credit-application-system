package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import me.dio.credit.application.system.dto.request.CreditDto
import me.dio.credit.application.system.dto.request.CustomerDto
import me.dio.credit.application.system.entity.Address
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditResourceTest {
    @Autowired
    private lateinit var creditRepository: CreditRepository

    @Autowired
    private lateinit var customerRepository: CustomerRepository

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
    fun `should save a credit and return 201 status`(){
       //given
        val customerSaved = customerRepository.save(buildCustomer(cpf = "11122233311", email = "save@gmail.com"))
        val credit = buildCredit(customerId = customerSaved.id)
        val valueAsString: String = objectMapper.writeValueAsString(credit)
        //when
        //then
        mockMvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valueAsString)
        )
                .andExpect(MockMvcResultMatchers.status().isCreated)

                .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `should not save credit with dayFirstInstallment is past`(){
        //given
        val customerSaved = customerRepository.save(buildCustomer(cpf = "11122233355", email = "dayFirstInstallment@gmail.com"))
        val credit = buildCredit(customerId = customerSaved.id, dayFirstInstallment = LocalDate.of(2022, 10, 12))
        val valueAsString: String = objectMapper.writeValueAsString(credit)
        //when
        //then
        mockMvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valueAsString)
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

                .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `should not save credit with numberOfInstallments bigger then 48`(){
        //given
        val customerSaved = customerRepository.save(buildCustomer(cpf = "11122233344", email = "numberOfInstallments@gmail.com"))
        val credit = buildCredit(customerId = 1L, numberOfInstallments = 50)
        val valueAsString: String = objectMapper.writeValueAsString(credit)
        //when
        //then
        mockMvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valueAsString)
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

                .andDo(MockMvcResultHandlers.print())

    }

    private fun buildCredit(
            creditValue: BigDecimal = BigDecimal.valueOf(1000.0),
            dayFirstInstallment: LocalDate = LocalDate.of(2024, Month.MARCH, 30),
            numberOfInstallments: Int = 10,
            customerId: Long?
    ): CreditDto = CreditDto(
            creditValue = creditValue,
            dayFirstOfInstallment = dayFirstInstallment,
            numberOfInstallments = numberOfInstallments,
            customerId = customerId!!
    )
    private fun buildCustomer(
            firstName: String = "José",
            lastName: String = "Santos",
            cpf: String = "12365478950",
            email: String = "jose@gmail.com",
            password: String = "12345",
            zipCode: String = "12345",
            street: String = "Rua 1",
            income: BigDecimal = BigDecimal.valueOf(1000.0),
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
    )

}