package br.edu.fatecpg.appkeys.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.appkeys.R
import br.edu.fatecpg.appkeys.model.dto.ReservationDTO
import br.edu.fatecpg.appkeys.model.dto.ReservationListResponseDTO
import br.edu.fatecpg.appkeys.model.entity.reservaEntity

class PerfilAdapter(private var reservas: List<ReservationDTO>, private val onCancelarClick: (ReservationDTO) -> Unit = {}):
    RecyclerView.Adapter<PerfilAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val txvHorarioReserva = itemView.findViewById<TextView>(R.id.txv_horario_reserva)
        val txvSalaReserva = itemView.findViewById<TextView>(R.id.txv_sala_reserva)
        val btnCancelar = itemView.findViewById<Button>(R.id.btn_cancelar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reserva_perfil,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reservas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val horarioInicio = reservas[position].inicioReserva?.substring(11,16)
        val horarioFinal = reservas[position].fimReserva?.substring(11,16)
        holder.txvHorarioReserva.text = "${horarioInicio} - ${horarioFinal}"
        holder.txvSalaReserva.text = reservas[position].nomeSala

        holder.btnCancelar.setOnClickListener {
            onCancelarClick(reservas[position])
        }
    }

    fun updateAdapter(reservasNovas: List<ReservationDTO>) {
        reservas = reservasNovas
        notifyDataSetChanged()
    }
}