package br.edu.fatecpg.appkeys.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.appkeys.R
import br.edu.fatecpg.appkeys.model.dto.ReservationListResponseDTO
import br.edu.fatecpg.appkeys.model.entity.reservaEntity

class ReservasAdapter (private var reservas: List<ReservationListResponseDTO>):
    RecyclerView.Adapter<ReservasAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imgPerfil = itemView.findViewById<ImageView>(R.id.imv_perfil)
        val txvNomeReserva = itemView.findViewById<TextView>(R.id.txv_nome_reservado_por2)
        val txvHorarioReserva = itemView.findViewById<TextView>(R.id.txv_horario_reserva)
        val txvSalaReserva = itemView.findViewById<TextView>(R.id.txv_sala_reserva)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reserva,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reservas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imgPerfil.setImageURI(Uri.parse(reservas[position].linkImagemUsuario))
        val horarioInicio = reservas[position].horaInicioReserva
        val horarioFinal = reservas[position].horaFimReserva
        holder.txvHorarioReserva.text = "${horarioInicio} - ${horarioFinal}"
        holder.txvSalaReserva.text = reservas[position].nomeSala
        holder.txvNomeReserva.text = reservas[position].proprietario
    }

    fun updateAdapter(reservasNovas: List<ReservationListResponseDTO>) {
        reservas = reservasNovas
        notifyDataSetChanged()
    }
}