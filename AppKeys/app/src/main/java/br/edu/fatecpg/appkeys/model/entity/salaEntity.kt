package br.edu.fatecpg.appkeys.model.entity

import br.edu.fatecpg.appkeys.enums.StatusSalaEnum
import java.io.Serializable

data class salaEntity(
    var cdSala: Int,
    var nome:String="",
    var descricao:String="",
    var statusSala: StatusSalaEnum
): Serializable