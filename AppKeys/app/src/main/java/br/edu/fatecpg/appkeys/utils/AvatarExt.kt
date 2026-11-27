package br.edu.fatecpg.appkeys.utils

import android.widget.ImageView
import coil.load
import coil.transform.CircleCropTransformation

private const val URL_BASE = "http://10.235.29.95:8080/"

fun ImageView.carregarAvatar(nome: String, link: String?) {
    val fallback = InicialDrawable(nome)

    if (link.isNullOrBlank() || link.contains("foto-padrao")) {
        setImageDrawable(fallback)
    } else {
        load(if (link.startsWith("http")) link else URL_BASE + link) {
            transformations(CircleCropTransformation())
            placeholder(fallback)
            error(fallback)
        }
    }
}