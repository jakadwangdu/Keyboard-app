package app.aura.keyboard.ui

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import app.aura.keyboard.R
import app.aura.keyboard.logic.ClipEntry
import app.aura.keyboard.model.KeyboardPalette

/**
 * Inline clipboard history drawer (up to 20 clips with pinning).
 */
class ClipboardPanelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    interface Listener {
        fun onPasteClip(text: String)
        fun onTogglePin(id: String)
        fun onDeleteClip(id: String)
        fun onClearUnpinned()
        fun onCloseClipboard()
    }

    var listener: Listener? = null

    private val density = context.resources.displayMetrics.density
    private val headerTitle: TextView
    private val backBtn: TextView
    private val clearBtn: TextView
    private val listContainer: LinearLayout
    private val emptyView: TextView

    private var palette = KeyboardPalette.DARK_ESPRESSO
    private var clips: List<ClipEntry> = emptyList()

    init {
        orientation = VERTICAL
        val padH = (12f * density).toInt()
        val padV = (8f * density).toInt()
        setPadding(padH, padV, padH, padV)

        val headerRow = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        backBtn = TextView(context).apply {
            text = context.getString(R.string.emoji_abc)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            contentDescription = context.getString(R.string.clipboard_back_cd)
            setPadding((12f * density).toInt(), (6f * density).toInt(), (12f * density).toInt(), (6f * density).toInt())
            setOnClickListener { listener?.onCloseClipboard() }
        }
        headerRow.addView(backBtn)

        headerTitle = TextView(context).apply {
            text = context.getString(R.string.clipboard_title)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }
        headerRow.addView(headerTitle, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))

        clearBtn = TextView(context).apply {
            text = context.getString(R.string.clipboard_clear_unpinned)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            gravity = Gravity.CENTER
            setPadding((12f * density).toInt(), (6f * density).toInt(), (12f * density).toInt(), (6f * density).toInt())
            setOnClickListener { listener?.onClearUnpinned() }
        }
        headerRow.addView(clearBtn)

        addView(headerRow, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            bottomMargin = (8f * density).toInt()
        })

        emptyView = TextView(context).apply {
            text = context.getString(R.string.clipboard_empty)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            gravity = Gravity.CENTER
            setPadding((16f * density).toInt(), (24f * density).toInt(), (16f * density).toInt(), (24f * density).toInt())
        }
        addView(emptyView, LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f))

        val scrollView = ScrollView(context).apply {
            isVerticalScrollBarEnabled = false
        }
        listContainer = LinearLayout(context).apply {
            orientation = VERTICAL
        }
        scrollView.addView(listContainer)
        addView(scrollView, LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f))

        render()
    }

    fun update(newPalette: KeyboardPalette, newClips: List<ClipEntry>) {
        palette = newPalette
        clips = newClips
        render()
    }

    private fun render() {
        headerTitle.setTextColor(palette.keyText)
        backBtn.setTextColor(palette.keyText)
        backBtn.background = roundedRect(palette.modifierFill, 10f)
        clearBtn.setTextColor(palette.mutedText)
        clearBtn.background = roundedRect(palette.modifierFill, 10f)
        clearBtn.visibility = if (clips.any { !it.pinned }) VISIBLE else INVISIBLE

        emptyView.setTextColor(palette.mutedText)
        listContainer.removeAllViews()

        if (clips.isEmpty()) {
            emptyView.visibility = VISIBLE
            return
        }
        emptyView.visibility = GONE

        val rowPadH = (12f * density).toInt()
        val rowPadV = (10f * density).toInt()
        val actionPad = (8f * density).toInt()

        for (entry in clips) {
            val row = LinearLayout(context).apply {
                orientation = HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(rowPadH, rowPadV, rowPadH, rowPadV)
                background = roundedRect(
                    if (entry.pinned) palette.modifierFill else palette.keyFill,
                    12f,
                    strokeColor = if (entry.pinned) palette.emojiAccent else 0
                )
                setOnClickListener { listener?.onPasteClip(entry.text) }
            }

            val textCol = LinearLayout(context).apply {
                orientation = VERTICAL
            }
            if (entry.pinned) {
                val badge = TextView(context).apply {
                    text = context.getString(R.string.clipboard_pinned_badge)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(palette.emojiAccent)
                }
                textCol.addView(badge)
            }
            val clipTv = TextView(context).apply {
                text = entry.text
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTextColor(palette.keyText)
                maxLines = 2
                ellipsize = TextUtils.TruncateAt.END
            }
            textCol.addView(clipTv)
            row.addView(textCol, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))

            val pinBtn = TextView(context).apply {
                text = if (entry.pinned) "★" else "☆"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setTextColor(if (entry.pinned) palette.emojiAccent else palette.mutedText)
                contentDescription = context.getString(R.string.clipboard_pin_cd)
                setPadding(actionPad, actionPad, actionPad, actionPad)
                setOnClickListener { listener?.onTogglePin(entry.id) }
            }
            row.addView(pinBtn)

            val deleteBtn = TextView(context).apply {
                text = "✕"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTextColor(palette.mutedText)
                contentDescription = context.getString(R.string.clipboard_delete_cd)
                setPadding(actionPad, actionPad, actionPad, actionPad)
                setOnClickListener { listener?.onDeleteClip(entry.id) }
            }
            row.addView(deleteBtn)

            listContainer.addView(row, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = (6f * density).toInt()
            })
        }
    }

    private fun roundedRect(fill: Int, radiusDp: Float, strokeColor: Int = 0): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = radiusDp * density
            setColor(fill)
            if (strokeColor != 0) {
                setStroke((1f * density).toInt(), strokeColor)
            }
        }
    }
}
