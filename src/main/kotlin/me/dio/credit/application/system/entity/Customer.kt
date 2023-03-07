package me.dio.credit.application.system.entity

data class Customer(
  var firstName: String = "",
  var lastName: String = "",
  val cpf: String,
  var email: String = "",
  var password: String = "",
  var address: Address = Address(),
  var credits: List<Credit> = mutableListOf(),
  val id: Long? = null
)
