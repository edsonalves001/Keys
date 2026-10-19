package br.edu.fatecpg.appkeys.dao

import br.edu.fatecpg.appkeys.model.salaModel

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