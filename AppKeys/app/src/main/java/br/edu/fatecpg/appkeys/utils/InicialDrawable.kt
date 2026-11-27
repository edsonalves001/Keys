package br.edu.fatecpg.appkeys.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import kotlin.math.min

class InicialDrawable(nome: String) : Drawable() {
    override fun getIntrinsicWidth() = 120
    override fun getIntrinsicHeight() = 120
    private val inicial = nome.trim().firstOrNull()?.uppercase() ?: "?"

    private val paintFundo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = CORES[Math.floorMod(nome.hashCode(), CORES.size)]
    }

    private val paintTexto = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    override fun draw(canvas: Canvas) {
        val area = bounds
        val raio = min(area.width(), area.height()) / 2f
        val cx = area.exactCenterX()
        val cy = area.exactCenterY()

        canvas.drawCircle(cx, cy, raio, paintFundo)

        paintTexto.textSize = raio // letra ocupa cerca de metade do círculo
        val y = cy - (paintTexto.descent() + paintTexto.ascent()) / 2
        canvas.drawText(inicial, cx, y, paintTexto)
    }

    override fun setAlpha(alpha: Int) {
        paintFundo.alpha = alpha
        paintTexto.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paintFundo.colorFilter = colorFilter
        paintTexto.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity() = PixelFormat.TRANSLUCENT

    companion object {
        private val CORES = intArrayOf(
            0xFFE57373.toInt(), 0xFFF06292.toInt(), 0xFFBA68C8.toInt(),
            0xFF7986CB.toInt(), 0xFF4FC3F7.toInt(), 0xFF4DB6AC.toInt(),
            0xFF81C784.toInt(), 0xFFFFB74D.toInt(), 0xFFA1887F.toInt()
        )
    }
}