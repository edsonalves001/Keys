package br.edu.fatecpg.aulamobile1.telas_segundo_sprint.dao

import br.edu.fatecpg.aulamobile1.telas_segundo_sprint.model.salaModel

class salaDao {
    companion object{
        private val salas = mutableListOf<salaModel>()
        fun salvarSala(room:String): String{
            salas.add(salaModel(room));
            return "Sala salva";
        }
        fun buscarSala(): List<salaModel>{
            return salas;
        }
    }
}