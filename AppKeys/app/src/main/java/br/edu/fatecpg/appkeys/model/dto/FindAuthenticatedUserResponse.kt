package br.edu.fatecpg.appkeys.model.dto

data class FindAuthenticatedUserResponse(
    val cdUsuario: String,
    val nm_usuario: String,
    val nm_email_usuario: String,
    val nm_link_imagem_usuario: String
)
