package br.edu.fatecpg.appkeys.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.appkeys.R
import br.edu.fatecpg.appkeys.model.dto.ReservationListResponseDTO
import br.edu.fatecpg.appkeys.utils.InicialDrawable
import coil.load
import coil.transform.CircleCropTransformation

private const val URL_BASE = "http://10.235.29.95:8080"

class ReservasAdapter(private var reservas: List<ReservationListResponseDTO>) :
    RecyclerView.Adapter<ReservasAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPerfil = itemView.findViewById<ImageView>(R.id.imv_perfil)
        val txvNomeReserva = itemView.findViewById<TextView>(R.id.txv_nome_reservado_por2)
        val txvHorarioReserva = itemView.findViewById<TextView>(R.id.txv_horario_reserva)
        val txvSalaReserva = itemView.findViewById<TextView>(R.id.txv_sala_reserva)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reserva, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reservas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reserva = reservas[position]

        val fallback = InicialDrawable(reserva.proprietario)
        val link = reserva.linkImagemUsuario

        if (link.isNullOrBlank() || link.contains("foto-padrao")) {
            holder.imgPerfil.setImageDrawable(fallback)
        } else {
            val urlCompleta = if (link.startsWith("http")) link else URL_BASE + link

            holder.imgPerfil.load(urlCompleta) {
                transformations(CircleCropTransformation())
                placeholder(fallback)
                error(fallback)
            }
        }

        holder.txvHorarioReserva.text = "${reserva.horaInicioReserva} - ${reserva.horaFimReserva}"
        holder.txvSalaReserva.text = reserva.nomeSala
        holder.txvNomeReserva.text = reserva.proprietario
    }
    fun updateAdapter(reservasNovas: List<ReservationListResponseDTO>) {
        reservas = reservasNovas
        notifyDataSetChanged()
    }
}