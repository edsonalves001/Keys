package br.edu.fatecpg.appkeys.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.appkeys.R
import br.edu.fatecpg.appkeys.model.salaModel

class salasadapter (private val salas: List<salaModel>):
    RecyclerView.Adapter<salasadapter.ViewHolder>(){
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val nomeSala = itemView.findViewById<TextView>(R.id.txv_titulo_sala)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sala_disponivel,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return salas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nomeSala.text = salas[position].nome
    }
}