package app.aura.keyboard.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.widget.LinearLayout
import app.aura.keyboard.model.KeyboardPalette

/**
 * Root rounded-top card container for the keyboard (34dp top corners, vertical gradient,
 * subtle top rim highlight). Allocates nothing in draw calls.
 */
class AuraCardLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val density = context.resources.displayMetrics.density
    private val topCornerRadiusPx = 34f * density
    private val rimStrokePx = 1f * density

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = rimStrokePx
    }

    private val cardPath = Path()
    private val rimPath = Path()
    private val cardRect = RectF()
    private val radii = floatArrayOf(
        topCornerRadiusPx, topCornerRadiusPx,
        topCornerRadiusPx, topCornerRadiusPx,
        0f, 0f,
        0f, 0f
    )

    private var palette: KeyboardPalette = KeyboardPalette.DARK_ESPRESSO

    init {
        orientation = VERTICAL
        setWillNotDraw(false)
        applyPalette(palette)
    }

    fun applyPalette(newPalette: KeyboardPalette) {
        palette = newPalette
        rimPaint.color = newPalette.cardRim
        rebuildShader(width, height)
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rebuildPaths(w, h)
        rebuildShader(w, h)
    }

    private fun rebuildPaths(w: Int, h: Int) {
        cardPath.reset()
        cardRect.set(0f, 0f, w.toFloat(), h.toFloat())
        cardPath.addRoundRect(cardRect, radii, Path.Direction.CW)

        rimPath.reset()
        val halfStroke = rimStrokePx * 0.5f
        val r = topCornerRadiusPx
        rimPath.moveTo(halfStroke, r)
        rimPath.arcTo(
            halfStroke,
            halfStroke,
            2f * r - halfStroke,
            2f * r - halfStroke,
            180f,
            90f,
            false
        )
        rimPath.lineTo(w - r, halfStroke)
        rimPath.arcTo(
            w - 2f * r + halfStroke,
            halfStroke,
            w - halfStroke,
            2f * r - halfStroke,
            270f,
            90f,
            false
        )
    }

    private fun rebuildShader(w: Int, h: Int) {
        if (w <= 0 || h <= 0) {
            bgPaint.shader = null
            bgPaint.color = palette.cardTop
            return
        }
        bgPaint.shader = LinearGradient(
            0f,
            0f,
            0f,
            h.toFloat(),
            palette.cardTop,
            palette.cardBottom,
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(cardPath, bgPaint)
        canvas.drawPath(rimPath, rimPaint)
    }
}
