package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CreditRepository
import org.assertj.core.api.Assertions
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
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditResourceTest {

    @Autowired
    private lateinit var creditRepository: CreditRepository

    @Autowired
    private lateinit var mockMvc: MockMvc;

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
    fun `should create a credit and return 201` (){
        //given
        val customer = buildCustomerDTO()
        val creditDto: Credit = buildCreditDTO(customer = customer)
        val valueAsString = objectMapper.writeValueAsString(creditDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Gil"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Silva"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("100011100"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("gilson@email.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("10000.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("77777"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Gil Street, 123"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andDo(MockMvcResultHandlers.print())
        
    }
    @Test
    fun `should find all credits by customer id` (){

        //given
        val customerId = 1L
        //when
        val creditList: List<Credit> = creditRepository.findAllByCustomerId(customerId)
        //then
        Assertions.assertThat(creditList).isNotEmpty
        Assertions.assertThat(creditList.size).isEqualTo(1)
    }

    @Test
    fun `should find credit by credit code`() {
        //given
        val creditCode = UUID.fromString("aa547c0f-9a6a-451f-8c89-afddce916a29")
        //when
        val fakeCredit: Credit = creditRepository.findByCreditCode(creditCode)!!
        //then
        Assertions.assertThat(fakeCredit).isNotNull
    }


    private fun buildCustomerDTO(
            firstName: String = "Gil",
            lastName: String = "Silva",
            cpf: String = "100011100",
            email: String = "gilson@email.com",
            password: String = "77777",
            zipCode: String = "77777",
            street: String = "Gil Street",
            income: BigDecimal = BigDecimal.valueOf(10000.0),
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

    private fun buildCreditDTO(
            creditValue: BigDecimal = BigDecimal.valueOf(200000),
            dayFirstInstallment: LocalDate = LocalDate.of(2023, Month.JUNE, 12),
            numberOfInstallments: Int = 7,
            customer: Customer
    ): Credit = Credit(
            creditValue = creditValue,
            dayFirstInstallment = dayFirstInstallment,
            numberOfInstallments = numberOfInstallments,
            customer = customer
    )
}