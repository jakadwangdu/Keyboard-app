package app.aura.keyboard.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import app.aura.keyboard.R
import app.aura.keyboard.model.KeySizePreset
import app.aura.keyboard.model.KeyboardPalette
import app.aura.keyboard.model.PanelMode

/**
 * Toolbar above the keys containing Clipboard, Emoji, Size selector (14/16/18), and Voice typing.
 */
class ToolbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    interface Listener {
        fun onClipboardClicked()
        fun onEmojiClicked()
        fun onVoiceClicked()
        fun onSizeSelected(preset: KeySizePreset)
    }

    var listener: Listener? = null

    private val density = context.resources.displayMetrics.density
    private val clipboardBtn: ToolbarIconButton
    private val emojiBtn: ToolbarIconButton
    private val voiceBtn: ToolbarIconButton
    private val sizeButtons = LinkedHashMap<KeySizePreset, TextView>(3)

    private var palette = KeyboardPalette.DARK_ESPRESSO
    private var panelMode = PanelMode.KEYBOARD
    private var sizePreset = KeySizePreset.MEDIUM

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        val padH = (16f * density).toInt()
        val padTop = (10f * density).toInt()
        val padBottom = (4f * density).toInt()
        setPadding(padH, padTop, padH, padBottom)

        val iconSize = (38f * density).toInt()

        clipboardBtn = ToolbarIconButton(context, ToolbarIconButton.IconType.CLIPBOARD).apply {
            contentDescription = context.getString(R.string.toolbar_clipboard_cd)
            setOnClickListener { listener?.onClipboardClicked() }
        }
        addView(clipboardBtn, LayoutParams(iconSize, iconSize))

        val gap = (8f * density).toInt()
        emojiBtn = ToolbarIconButton(context, ToolbarIconButton.IconType.EMOJI).apply {
            contentDescription = context.getString(R.string.toolbar_emoji_cd)
            setOnClickListener { listener?.onEmojiClicked() }
        }
        addView(emojiBtn, LayoutParams(iconSize, iconSize).apply { marginStart = gap })

        // Flexible spacer
        addView(View(context), LayoutParams(0, 1, 1f))

        // Size presets (14, 16, 18) mirroring the reference layout
        val sizeContainer = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val sizeBtnWidth = (32f * density).toInt()
        val sizeBtnHeight = (30f * density).toInt()
        for (preset in KeySizePreset.entries) {
            val tv = TextView(context).apply {
                text = preset.label
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                gravity = Gravity.CENTER
                contentDescription = context.getString(R.string.toolbar_size_cd, preset.label)
                setOnClickListener { listener?.onSizeSelected(preset) }
            }
            sizeButtons[preset] = tv
            sizeContainer.addView(tv, LayoutParams(sizeBtnWidth, sizeBtnHeight).apply {
                marginEnd = (2f * density).toInt()
            })
        }
        addView(sizeContainer, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            marginEnd = (10f * density).toInt()
        })

        // Voice typing circle button on the right
        val micSize = (40f * density).toInt()
        voiceBtn = ToolbarIconButton(context, ToolbarIconButton.IconType.MIC).apply {
            contentDescription = context.getString(R.string.toolbar_voice_cd)
            setOnClickListener { listener?.onVoiceClicked() }
        }
        addView(voiceBtn, LayoutParams(micSize, micSize))

        refreshStyles()
    }

    fun updateState(
        newPalette: KeyboardPalette,
        newPanelMode: PanelMode,
        newSizePreset: KeySizePreset
    ) {
        palette = newPalette
        panelMode = newPanelMode
        sizePreset = newSizePreset
        refreshStyles()
    }

    private fun refreshStyles() {
        clipboardBtn.updateStyle(
            bgColor = if (panelMode == PanelMode.CLIPBOARD) palette.accentFill else palette.toolbarCircle,
            fgColor = if (panelMode == PanelMode.CLIPBOARD) palette.textOnAccent else palette.keyText
        )
        emojiBtn.updateStyle(
            bgColor = if (panelMode == PanelMode.EMOJI) palette.accentFill else palette.toolbarCircle,
            fgColor = if (panelMode == PanelMode.EMOJI) palette.textOnAccent else palette.emojiAccent
        )
        voiceBtn.updateStyle(
            bgColor = palette.toolbarCircle,
            fgColor = palette.keyText
        )

        for ((preset, tv) in sizeButtons) {
            val selected = preset == sizePreset
            tv.setTextColor(if (selected) palette.keyText else palette.mutedText)
            tv.typeface = if (selected) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            tv.background = if (selected) {
                GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 15f * density
                    setColor(palette.toolbarCircle)
                }
            } else {
                null
            }
        }
    }

    private class ToolbarIconButton(
        context: Context,
        private val iconType: IconType
    ) : View(context) {

        enum class IconType { CLIPBOARD, EMOJI, MIC }

        private val density = context.resources.displayMetrics.density
        private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1.7f * density
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val rect = RectF()

        init {
            isClickable = true
            isFocusable = true
        }

        fun updateStyle(bgColor: Int, fgColor: Int) {
            bgPaint.color = bgColor
            strokePaint.color = fgColor
            fillPaint.color = fgColor
            invalidate()
        }

        override fun onDraw(canvas: Canvas) {
            val cx = width * 0.5f
            val cy = height * 0.5f
            val radius = (minOf(width, height) * 0.5f) - density
            canvas.drawCircle(cx, cy, radius, bgPaint)

            val s = density
            when (iconType) {
                IconType.CLIPBOARD -> {
                    // Rounded board body
                    rect.set(cx - 5.8f * s, cy - 5.5f * s, cx + 5.8f * s, cy + 7.2f * s)
                    canvas.drawRoundRect(rect, 2f * s, 2f * s, strokePaint)
                    // Top clip tab
                    rect.set(cx - 2.8f * s, cy - 7.5f * s, cx + 2.8f * s, cy - 4.3f * s)
                    canvas.drawRoundRect(rect, 1.2f * s, 1.2f * s, fillPaint)
                    // Subtle lines
                    canvas.drawLine(cx - 2.8f * s, cy - 0.5f * s, cx + 2.8f * s, cy - 0.5f * s, strokePaint)
                    canvas.drawLine(cx - 2.8f * s, cy + 3.0f * s, cx + 1.5f * s, cy + 3.0f * s, strokePaint)
                }
                IconType.EMOJI -> {
                    val r = 6.8f * s
                    canvas.drawCircle(cx, cy, r, strokePaint)
                    canvas.drawCircle(cx - 2.3f * s, cy - 1.5f * s, 1.0f * s, fillPaint)
                    canvas.drawCircle(cx + 2.3f * s, cy - 1.5f * s, 1.0f * s, fillPaint)
                    rect.set(cx - 3.4f * s, cy - 1.8f * s, cx + 3.4f * s, cy + 3.8f * s)
                    canvas.drawArc(rect, 20f, 140f, false, strokePaint)
                }
                IconType.MIC -> {
                    // Mic capsule
                    rect.set(cx - 2.6f * s, cy - 7.2f * s, cx + 2.6f * s, cy + 1.2f * s)
                    canvas.drawRoundRect(rect, 2.6f * s, 2.6f * s, strokePaint)
                    // Mic cradle arc
                    rect.set(cx - 5.4f * s, cy - 3.8f * s, cx + 5.4f * s, cy + 4.4f * s)
                    canvas.drawArc(rect, 10f, 160f, false, strokePaint)
                    // Stem + base
                    canvas.drawLine(cx, cy + 4.5f * s, cx, cy + 7.4f * s, strokePaint)
                    canvas.drawLine(cx - 3.2f * s, cy + 7.4f * s, cx + 3.2f * s, cy + 7.4f * s, strokePaint)
                }
            }
        }
    }
}
