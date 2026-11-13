package app.aura.keyboard.ui

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import app.aura.keyboard.R
import app.aura.keyboard.model.EmojiCatalog
import app.aura.keyboard.model.KeyboardPalette

/**
 * Inline emoji drawer with a Recents row, category tabs, emoji grid, ABC toggle, and Backspace.
 */
class EmojiPanelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    interface Listener {
        fun onEmojiPicked(emoji: String)
        fun onBackspaceTap()
        fun onCloseEmoji()
    }

    var listener: Listener? = null

    private val density = context.resources.displayMetrics.density
    private val recentsRow: LinearLayout
    private val recentsEmptyLabel: TextView
    private val gridContainer: LinearLayout
    private val categoryTabsRow: LinearLayout
    private val abcBtn: TextView
    private val backspaceBtn: TextView

    private var palette = KeyboardPalette.DARK_ESPRESSO
    private var recentEmojis: List<String> = emptyList()
    private var selectedGroupIndex = 0

    init {
        orientation = VERTICAL
        val padH = (10f * density).toInt()
        val padV = (6f * density).toInt()
        setPadding(padH, padV, padH, padV)

        // 1. Recents horizontal row
        val recentsScroll = HorizontalScrollView(context).apply {
            isHorizontalScrollBarEnabled = false
        }
        recentsRow = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        recentsEmptyLabel = TextView(context).apply {
            text = context.getString(R.string.emoji_recents_empty)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setPadding((8f * density).toInt(), (4f * density).toInt(), (8f * density).toInt(), (4f * density).toInt())
        }
        recentsScroll.addView(recentsRow)
        addView(recentsScroll, LayoutParams(LayoutParams.MATCH_PARENT, (40f * density).toInt()).apply {
            bottomMargin = (4f * density).toInt()
        })

        // 2. Scrollable emoji grid
        val gridScroll = ScrollView(context).apply {
            isVerticalScrollBarEnabled = false
        }
        gridContainer = LinearLayout(context).apply {
            orientation = VERTICAL
        }
        gridScroll.addView(gridContainer)
        addView(gridScroll, LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f))

        // 3. Bottom row: ABC | Category Tabs | Backspace
        val bottomRow = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        abcBtn = TextView(context).apply {
            text = context.getString(R.string.emoji_abc)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setOnClickListener { listener?.onCloseEmoji() }
        }
        bottomRow.addView(abcBtn, LayoutParams((52f * density).toInt(), (38f * density).toInt()))

        categoryTabsRow = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
        }
        bottomRow.addView(categoryTabsRow, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))

        backspaceBtn = TextView(context).apply {
            text = "⌫"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
            gravity = Gravity.CENTER
            contentDescription = context.getString(R.string.emoji_backspace_cd)
            setOnClickListener { listener?.onBackspaceTap() }
        }
        bottomRow.addView(backspaceBtn, LayoutParams((52f * density).toInt(), (38f * density).toInt()))

        addView(bottomRow, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            topMargin = (4f * density).toInt()
        })

        render()
    }

    fun update(newPalette: KeyboardPalette, newRecents: List<String>) {
        palette = newPalette
        recentEmojis = newRecents
        render()
    }

    private fun render() {
        abcBtn.setTextColor(palette.keyText)
        abcBtn.background = roundedRect(palette.modifierFill, 9f)
        backspaceBtn.setTextColor(palette.keyText)
        backspaceBtn.background = roundedRect(palette.modifierFill, 9f)

        // Render Recents row
        recentsRow.removeAllViews()
        if (recentEmojis.isEmpty()) {
            recentsEmptyLabel.setTextColor(palette.mutedText)
            recentsRow.addView(recentsEmptyLabel)
        } else {
            val cellSize = (38f * density).toInt()
            for (emoji in recentEmojis) {
                val tv = TextView(context).apply {
                    text = emoji
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 21f)
                    gravity = Gravity.CENTER
                    setOnClickListener { listener?.onEmojiPicked(emoji) }
                }
                recentsRow.addView(tv, LayoutParams(cellSize, cellSize))
            }
        }

        // Render Category Tabs
        categoryTabsRow.removeAllViews()
        val tabSize = (36f * density).toInt()
        EmojiCatalog.groups.forEachIndexed { idx, group ->
            val selected = idx == selectedGroupIndex
            val tab = TextView(context).apply {
                text = group.iconLabel
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                gravity = Gravity.CENTER
                contentDescription = group.title
                setTextColor(if (selected) palette.keyText else palette.mutedText)
                background = if (selected) roundedRect(palette.keyFill, 8f) else null
                setOnClickListener {
                    selectedGroupIndex = idx
                    render()
                }
            }
            categoryTabsRow.addView(tab, LayoutParams(tabSize, tabSize))
        }

        // Render Grid (8 columns)
        gridContainer.removeAllViews()
        val activeGroup = EmojiCatalog.groups[selectedGroupIndex.coerceIn(0, EmojiCatalog.groups.lastIndex)]
        val columns = 8
        val emojis = activeGroup.emojis
        val cellHeight = (42f * density).toInt()
        var i = 0
        while (i < emojis.size) {
            val rowLayout = LinearLayout(context).apply {
                orientation = HORIZONTAL
            }
            for (c in 0 until columns) {
                if (i + c < emojis.size) {
                    val emoji = emojis[i + c]
                    val cell = TextView(context).apply {
                        text = emoji
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 23f)
                        gravity = Gravity.CENTER
                        setOnClickListener { listener?.onEmojiPicked(emoji) }
                    }
                    rowLayout.addView(cell, LayoutParams(0, cellHeight, 1f))
                } else {
                    rowLayout.addView(TextView(context), LayoutParams(0, cellHeight, 1f))
                }
            }
            gridContainer.addView(rowLayout, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            i += columns
        }
    }

    private fun roundedRect(fill: Int, radiusDp: Float): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = radiusDp * density
            setColor(fill)
        }
    }
}
