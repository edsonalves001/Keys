package br.edu.fatecpg.appkeys.model.dto

data class LoginRequestDTO(
  val nm_email_usuario: String,
  val nm_senha_usuario: String
)