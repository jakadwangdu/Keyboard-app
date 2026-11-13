package app.aura.keyboard.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.aura.keyboard.R
import app.aura.keyboard.logic.ClipboardStore
import app.aura.keyboard.model.KeySizePreset
import app.aura.keyboard.model.ThemeMode
import app.aura.keyboard.prefs.KeyboardPreferences

class SettingsActivity : ComponentActivity() {

    private lateinit var prefs: KeyboardPreferences
    private lateinit var clipboardStore: ClipboardStore

    private lateinit var statusEnabledChip: TextView
    private lateinit var statusSelectedChip: TextView
    private lateinit var testField: EditText
    private lateinit var soundSwitch: Switch
    private lateinit var hapticsSwitch: Switch

    private val themeButtons = LinkedHashMap<ThemeMode, TextView>(3)
    private val sizeButtons = LinkedHashMap<KeySizePreset, TextView>(3)

    private var density: Float = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)

        density = resources.displayMetrics.density
        prefs = KeyboardPreferences(this)
        clipboardStore = ClipboardStore(this)

        val rootScroll = ScrollView(this).apply {
            isFillViewport = true
            setBackgroundColor(COLOR_BG)
        }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val padH = dp(20)
            setPadding(padH, dp(24), padH, dp(32))
        }

        ViewCompat.setOnApplyWindowInsetsListener(rootScroll) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            container.setPadding(
                dp(20),
                dp(20) + bars.top,
                dp(20),
                dp(28) + bars.bottom
            )
            insets
        }

        // Header
        val titleTv = TextView(this).apply {
            text = getString(R.string.app_name)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            setTextColor(COLOR_CREAM)
        }
        container.addView(titleTv)

        val subtitleTv = TextView(this).apply {
            text = getString(R.string.settings_subtitle)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(COLOR_MUTED)
        }
        container.addView(subtitleTv, marginBottom(4, 16))

        // Live IME Status Row
        val statusRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        statusEnabledChip = createStatusChip(R.id.status_enabled_chip)
        statusSelectedChip = createStatusChip(R.id.status_selected_chip)
        statusRow.addView(statusEnabledChip, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { marginEnd = dp(8) })
        statusRow.addView(statusSelectedChip)
        container.addView(statusRow, marginBottom(0, 20))

        // Step 1: Enable Keyboard
        container.addView(
            buildActionCard(
                title = getString(R.string.step_1_title),
                desc = getString(R.string.step_1_desc),
                buttonText = getString(R.string.btn_enable_keyboard),
                buttonId = R.id.btn_enable_keyboard,
                isPrimary = true
            ) {
                try {
                    startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                } catch (_: Exception) {
                    Toast.makeText(this, R.string.step_1_desc, Toast.LENGTH_SHORT).show()
                }
            },
            marginBottom(0, 12)
        )

        // Step 2: Choose Keyboard
        container.addView(
            buildActionCard(
                title = getString(R.string.step_2_title),
                desc = getString(R.string.step_2_desc),
                buttonText = getString(R.string.btn_choose_keyboard),
                buttonId = R.id.btn_choose_keyboard,
                isPrimary = false
            ) {
                val imm = getSystemService(InputMethodManager::class.java)
                imm?.showInputMethodPicker()
            },
            marginBottom(0, 12)
        )

        // Step 3: Test Text Field
        val testCard = createCardLayout()
        testCard.addView(TextView(this).apply {
            text = getString(R.string.step_3_title)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(COLOR_CREAM)
        }, marginBottom(0, 10))

        testField = EditText(this).apply {
            id = R.id.test_input_field
            hint = getString(R.string.test_field_hint)
            setHintTextColor(COLOR_MUTED)
            setTextColor(COLOR_CREAM)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or
                InputType.TYPE_TEXT_FLAG_MULTI_LINE
            imeOptions = EditorInfo.IME_ACTION_DONE
            minHeight = dp(56)
            setPadding(dp(14), dp(12), dp(14), dp(12))
            background = roundedDrawable(COLOR_INPUT_BG, 14f, COLOR_STROKE)
        }
        testCard.addView(testField, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))
        container.addView(testCard, marginBottom(0, 16))

        // Preferences Card
        val prefsCard = createCardLayout()
        prefsCard.addView(TextView(this).apply {
            text = getString(R.string.preferences_title)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(COLOR_CREAM)
        }, marginBottom(0, 12))

        soundSwitch = createSwitchRow(
            parent = prefsCard,
            switchId = R.id.switch_sound,
            title = getString(R.string.pref_sound_title),
            desc = getString(R.string.pref_sound_desc),
            checked = prefs.soundEnabled
        ) { isChecked ->
            prefs.soundEnabled = isChecked
        }

        hapticsSwitch = createSwitchRow(
            parent = prefsCard,
            switchId = R.id.switch_haptics,
            title = getString(R.string.pref_haptics_title),
            desc = getString(R.string.pref_haptics_desc),
            checked = prefs.hapticsEnabled
        ) { isChecked ->
            prefs.hapticsEnabled = isChecked
        }

        // Theme selector
        prefsCard.addView(TextView(this).apply {
            text = getString(R.string.pref_theme_title)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(COLOR_CREAM)
        }, marginBottom(8, 8))

        val themeRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        listOf(
            ThemeMode.AUTO to getString(R.string.theme_auto),
            ThemeMode.DARK to getString(R.string.theme_dark),
            ThemeMode.LIGHT to getString(R.string.theme_light)
        ).forEachIndexed { idx, (mode, label) ->
            val pill = createPillOption(label) {
                prefs.themeMode = mode
                refreshPreferenceSelections()
            }
            themeButtons[mode] = pill
            themeRow.addView(pill, LinearLayout.LayoutParams(0, dp(48), 1f).apply {
                if (idx < 2) marginEnd = dp(8)
            })
        }
        prefsCard.addView(themeRow, marginBottom(0, 14))

        // Size selector
        prefsCard.addView(TextView(this).apply {
            text = getString(R.string.pref_size_title)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(COLOR_CREAM)
        }, marginBottom(0, 8))

        val sizeRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        KeySizePreset.entries.forEachIndexed { idx, preset ->
            val pill = createPillOption(preset.label) {
                prefs.keySizePreset = preset
                refreshPreferenceSelections()
            }
            sizeButtons[preset] = pill
            sizeRow.addView(pill, LinearLayout.LayoutParams(0, dp(48), 1f).apply {
                if (idx < KeySizePreset.entries.lastIndex) marginEnd = dp(8)
            })
        }
        prefsCard.addView(sizeRow, marginBottom(0, 16))

        // Clear unpinned clipboard button
        val clearBtn = Button(this).apply {
            id = R.id.btn_clear_clipboard
            text = getString(R.string.btn_clear_clipboard)
            isAllCaps = false
            setTextColor(COLOR_AMBER)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            minHeight = dp(48)
            background = roundedDrawable(COLOR_INPUT_BG, 12f, COLOR_STROKE)
            setOnClickListener {
                clipboardStore.clearUnpinned()
                Toast.makeText(this@SettingsActivity, R.string.clipboard_cleared_toast, Toast.LENGTH_SHORT).show()
            }
        }
        prefsCard.addView(clearBtn, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        container.addView(prefsCard)
        rootScroll.addView(container)
        setContentView(rootScroll)

        refreshPreferenceSelections()
        refreshImeStatus()
    }

    override fun onResume() {
        super.onResume()
        soundSwitch.isChecked = prefs.soundEnabled
        hapticsSwitch.isChecked = prefs.hapticsEnabled
        refreshPreferenceSelections()
        refreshImeStatus()
    }

    private fun refreshImeStatus() {
        val imm = getSystemService(InputMethodManager::class.java)
        val isEnabled = imm?.enabledInputMethodList?.any { it.packageName == packageName } == true
        val defaultIme = try {
            Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD).orEmpty()
        } catch (_: Exception) {
            ""
        }
        val isSelected = defaultIme.startsWith("$packageName/")

        statusEnabledChip.text = getString(
            if (isEnabled) R.string.status_enabled else R.string.status_not_enabled
        )
        statusEnabledChip.setTextColor(if (isEnabled) COLOR_DARK_INK else COLOR_MUTED)
        statusEnabledChip.background = roundedDrawable(
            fill = if (isEnabled) COLOR_AMBER else COLOR_CARD,
            radiusDp = 16f,
            stroke = if (isEnabled) 0 else COLOR_STROKE
        )

        statusSelectedChip.text = getString(
            if (isSelected) R.string.status_selected else R.string.status_not_selected
        )
        statusSelectedChip.setTextColor(if (isSelected) COLOR_DARK_INK else COLOR_MUTED)
        statusSelectedChip.background = roundedDrawable(
            fill = if (isSelected) COLOR_CREAM else COLOR_CARD,
            radiusDp = 16f,
            stroke = if (isSelected) 0 else COLOR_STROKE
        )
    }

    private fun refreshPreferenceSelections() {
        val currentTheme = prefs.themeMode
        for ((mode, pill) in themeButtons) {
            val active = mode == currentTheme
            pill.setTextColor(if (active) COLOR_DARK_INK else COLOR_CREAM)
            pill.typeface = if (active) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            pill.background = roundedDrawable(
                fill = if (active) COLOR_CREAM else COLOR_INPUT_BG,
                radiusDp = 12f,
                stroke = if (active) 0 else COLOR_STROKE
            )
        }

        val currentSize = prefs.keySizePreset
        for ((preset, pill) in sizeButtons) {
            val active = preset == currentSize
            pill.setTextColor(if (active) COLOR_DARK_INK else COLOR_CREAM)
            pill.typeface = if (active) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            pill.background = roundedDrawable(
                fill = if (active) COLOR_CREAM else COLOR_INPUT_BG,
                radiusDp = 12f,
                stroke = if (active) 0 else COLOR_STROKE
            )
        }
    }

    private fun buildActionCard(
        title: String,
        desc: String,
        buttonText: String,
        buttonId: Int,
        isPrimary: Boolean,
        onClick: () -> Unit
    ): LinearLayout {
        val card = createCardLayout()
        card.addView(TextView(this).apply {
            text = title
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(COLOR_CREAM)
        }, marginBottom(0, 4))

        card.addView(TextView(this).apply {
            text = desc
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setTextColor(COLOR_MUTED)
        }, marginBottom(0, 12))

        val button = Button(this).apply {
            id = buttonId
            text = buttonText
            isAllCaps = false
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            typeface = Typeface.DEFAULT_BOLD
            minHeight = dp(48)
            setTextColor(if (isPrimary) COLOR_DARK_INK else COLOR_CREAM)
            background = roundedDrawable(
                fill = if (isPrimary) COLOR_CREAM else COLOR_INPUT_BG,
                radiusDp = 14f,
                stroke = if (isPrimary) 0 else COLOR_STROKE
            )
            setOnClickListener { onClick() }
        }
        card.addView(button, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))
        return card
    }

    private fun createSwitchRow(
        parent: LinearLayout,
        switchId: Int,
        title: String,
        desc: String,
        checked: Boolean,
        onToggle: (Boolean) -> Unit
    ): Switch {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            minimumHeight = dp(52)
        }
        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        textCol.addView(TextView(this).apply {
            text = title
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(COLOR_CREAM)
        })
        textCol.addView(TextView(this).apply {
            text = desc
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextColor(COLOR_MUTED)
        })
        row.addView(textCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginEnd = dp(12)
        })

        val toggle = Switch(this).apply {
            id = switchId
            isChecked = checked
            minHeight = dp(48)
            minWidth = dp(48)
            thumbTintList = ColorStateList.valueOf(COLOR_CREAM)
            trackTintList = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                intArrayOf(COLOR_AMBER, COLOR_STROKE)
            )
            setOnCheckedChangeListener { _, isChecked -> onToggle(isChecked) }
        }
        row.addView(toggle)
        parent.addView(row, marginBottom(0, 10))
        return toggle
    }

    private fun createPillOption(label: String, onClick: () -> Unit): TextView {
        return TextView(this).apply {
            text = label
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            gravity = Gravity.CENTER
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
        }
    }

    private fun createStatusChip(chipId: Int): TextView {
        return TextView(this).apply {
            id = chipId
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(dp(12), dp(6), dp(12), dp(6))
        }
    }

    private fun createCardLayout(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedDrawable(COLOR_CARD, 20f, COLOR_STROKE)
        }
    }

    private fun roundedDrawable(fill: Int, radiusDp: Float, stroke: Int = 0): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = radiusDp * density
            setColor(fill)
            if (stroke != 0) {
                setStroke(dp(1), stroke)
            }
        }
    }

    private fun marginBottom(topDp: Int, bottomDp: Int): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = dp(topDp)
            bottomMargin = dp(bottomDp)
        }
    }

    private fun dp(value: Int): Int = (value * density).toInt()

    companion object {
        private const val COLOR_BG = 0xFF171513.toInt()
        private const val COLOR_CARD = 0xFF211E1B.toInt()
        private const val COLOR_INPUT_BG = 0xFF2B2622.toInt()
        private const val COLOR_STROKE = 0xFF3A342E.toInt()
        private const val COLOR_CREAM = 0xFFEFE7DA.toInt()
        private const val COLOR_MUTED = 0xFF9E9488.toInt()
        private const val COLOR_AMBER = 0xFFD9B98C.toInt()
        private const val COLOR_DARK_INK = 0xFF171513.toInt()
    }
}
