package app.aura.keyboard.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.os.SystemClock
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import app.aura.keyboard.model.ImeActionKind
import app.aura.keyboard.model.KeyKind
import app.aura.keyboard.model.KeySizePreset
import app.aura.keyboard.model.KeySpec
import app.aura.keyboard.model.KeyboardLayer
import app.aura.keyboard.model.KeyboardLayoutBuilder
import app.aura.keyboard.model.KeyboardPalette
import app.aura.keyboard.model.ShiftState
import kotlin.math.abs
import kotlin.math.hypot

/**
 * Single Canvas-rendered custom keyboard View.
 * - Zero per-key child Views.
 * - Zero allocations inside onDraw() and onTouchEvent().
 * - Multi-touch rollover using fixed primitive pointer arrays.
 * - Spacebar 180ms hold or >6dp drag trackpad (9dp/step X, 22dp/line Y).
 * - Backspace hold or swipe-left whole-word deletion.
 */
class KeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    interface Listener {
        fun onKeyPressedDown(kind: KeyKind)
        fun onCharacterKey(charText: String, chordShiftHeld: Boolean)
        fun onShiftTap(nowMs: Long)
        fun onShiftLongPress()
        fun onShiftChordReleased()
        fun onBackspaceTap()
        fun onDeleteWord()
        fun onSpaceTap(nowMs: Long)
        fun onTrackpadStepHorizontal(steps: Int)
        fun onTrackpadStepVertical(lines: Int)
        fun onLayerSwitch(targetLayer: KeyboardLayer)
        fun onEmojiKey()
        fun onReturnKey()
    }

    private class KeySlot {
        val bounds = RectF()
        var spec: KeySpec? = null
        var isPressed: Boolean = false
        var centerX: Float = 0f
        var centerY: Float = 0f
        var textBaselineY: Float = 0f
    }

    var listener: Listener? = null

    private val density = context.resources.displayMetrics.density

    private val horizontalPaddingPx = 6f * density
    private val topPaddingPx = 6f * density
    private val bottomPaddingPx = 10f * density
    private val keyGapXPx = 5f * density
    private val keyGapYPx = 8f * density
    private val keyCornerRadiusPx = 9f * density

    private val trackpadDragThresholdPx = 6f * density
    private val trackpadStepXPx = 9f * density
    private val trackpadStepYPx = 22f * density
    private val backspaceSwipeThresholdPx = 16f * density
    private val backspaceRepeatStepPx = 22f * density

    private var palette: KeyboardPalette = KeyboardPalette.DARK_ESPRESSO
    private var currentLayer: KeyboardLayer = KeyboardLayer.LETTERS
    private var shiftState: ShiftState = ShiftState.OFF
    private var imeActionKind: ImeActionKind = ImeActionKind.RETURN
    private var sizePreset: KeySizePreset = KeySizePreset.MEDIUM
    private var isTrackpadActive: Boolean = false

    private val keySlots = Array(MAX_KEYS) { KeySlot() }
    private var activeKeyCount = 0

    // Reusable Paints, Paths, and RectFs (allocated once)
    private val keyFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val trackpadBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.2f * density
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }
    private val modLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }
    private val iconStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.7f * density
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val iconFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val reusablePath = Path()
    private val reusableRect = RectF()
    private val labelFontMetrics = Paint.FontMetrics()
    private val modFontMetrics = Paint.FontMetrics()

    // Multi-touch primitive tracking arrays (zero allocations in onTouchEvent)
    private val pointerIds = IntArray(MAX_POINTERS) { INVALID_POINTER }
    private val pointerKeyIdx = IntArray(MAX_POINTERS) { -1 }
    private val pointerDownX = FloatArray(MAX_POINTERS)
    private val pointerDownY = FloatArray(MAX_POINTERS)
    private val pointerLastX = FloatArray(MAX_POINTERS)
    private val pointerLastY = FloatArray(MAX_POINTERS)
    private val pointerAccumX = FloatArray(MAX_POINTERS)
    private val pointerAccumY = FloatArray(MAX_POINTERS)
    private val pointerDownTime = LongArray(MAX_POINTERS)
    private val pointerCommitted = BooleanArray(MAX_POINTERS)
    private val pointerTrackpad = BooleanArray(MAX_POINTERS)
    private val pointerSwipeDelete = BooleanArray(MAX_POINTERS)

    private var activeSpacePointerSlot = -1
    private var activeBackspacePointerSlot = -1
    private var activeShiftPointerSlot = -1

    private val spaceHoldRunnable = Runnable {
        val slot = activeSpacePointerSlot
        if (slot >= 0 && pointerIds[slot] != INVALID_POINTER && !pointerCommitted[slot]) {
            pointerTrackpad[slot] = true
            pointerCommitted[slot] = true
            pointerAccumX[slot] = 0f
            pointerAccumY[slot] = 0f
            isTrackpadActive = true
            listener?.onKeyPressedDown(KeyKind.SPACE)
            invalidate()
        }
    }

    private val backspaceHoldRunnable = object : Runnable {
        override fun run() {
            val slot = activeBackspacePointerSlot
            if (slot >= 0 && pointerIds[slot] != INVALID_POINTER && !pointerSwipeDelete[slot]) {
                pointerCommitted[slot] = true
                listener?.onDeleteWord()
                postDelayed(this, BACKSPACE_REPEAT_MS)
            }
        }
    }

    private val shiftLongPressRunnable = Runnable {
        val slot = activeShiftPointerSlot
        if (slot >= 0 && pointerIds[slot] != INVALID_POINTER && !pointerCommitted[slot]) {
            pointerCommitted[slot] = true
            listener?.onShiftLongPress()
        }
    }

    init {
        isClickable = true
        isFocusable = false
        updateTextSizes()
        rebuildKeySlots()
    }

    fun configure(
        newPalette: KeyboardPalette,
        newLayer: KeyboardLayer,
        newShiftState: ShiftState,
        newImeActionKind: ImeActionKind,
        newSizePreset: KeySizePreset
    ) {
        val sizeChanged = sizePreset != newSizePreset
        val layoutDirty = currentLayer != newLayer ||
            shiftState != newShiftState ||
            imeActionKind != newImeActionKind ||
            sizeChanged

        palette = newPalette
        currentLayer = newLayer
        shiftState = newShiftState
        imeActionKind = newImeActionKind
        sizePreset = newSizePreset

        if (sizeChanged) {
            updateTextSizes()
            requestLayout()
        }
        if (layoutDirty) {
            rebuildKeySlots()
        }
        invalidate()
    }

    private fun updateTextSizes() {
        val sp = sizePreset.labelSp
        val metrics = context.resources.displayMetrics
        labelPaint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics)
        modLabelPaint.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            (sp - 3.5f).coerceAtLeast(12f),
            metrics
        )
        labelPaint.getFontMetrics(labelFontMetrics)
        modLabelPaint.getFontMetrics(modFontMetrics)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val rowHeightPx = sizePreset.keyHeightDp * density
        val desiredHeight = (topPaddingPx + 4f * rowHeightPx + 3f * keyGapYPx + bottomPaddingPx).toInt()
        val resolvedHeight = resolveSize(desiredHeight, heightMeasureSpec)
        setMeasuredDimension(desiredWidth, resolvedHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rebuildKeySlots()
    }

    private fun rebuildKeySlots() {
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) {
            val rows = KeyboardLayoutBuilder.buildRows(currentLayer, shiftState, imeActionKind)
            var idx = 0
            for (row in rows) {
                for (spec in row) {
                    if (idx >= MAX_KEYS) break
                    keySlots[idx].spec = spec
                    keySlots[idx].bounds.setEmpty()
                    keySlots[idx].isPressed = false
                    idx++
                }
            }
            activeKeyCount = idx
            return
        }

        val rows = KeyboardLayoutBuilder.buildRows(currentLayer, shiftState, imeActionKind)
        val rowCount = rows.size
        val usableWidth = (w - 2f * horizontalPaddingPx).coerceAtLeast(1f)
        val usableHeight = (h - topPaddingPx - bottomPaddingPx - (rowCount - 1) * keyGapYPx).coerceAtLeast(1f)
        val keyHeight = usableHeight / rowCount

        // Identical standard key width based on the 10-key top row (Q-P / 1-0 / symbols)
        val standardKeyWidth = (usableWidth - 9f * keyGapXPx) / 10f

        var slotIndex = 0
        for (r in 0 until rowCount) {
            val row = rows[r]
            val top = topPaddingPx + r * (keyHeight + keyGapYPx)
            val bottom = top + keyHeight

            if (currentLayer == KeyboardLayer.NUMBER_PAD) {
                val padWidth = (usableWidth - 3f * keyGapXPx) / 4f
                for (c in row.indices) {
                    if (slotIndex >= MAX_KEYS) break
                    val left = horizontalPaddingPx + c * (padWidth + keyGapXPx)
                    val right = left + padWidth
                    assignSlot(slotIndex++, row[c], left, top, right, bottom)
                }
                continue
            }

            when (r) {
                0 -> {
                    // 10 keys filling usableWidth
                    for (c in row.indices) {
                        if (slotIndex >= MAX_KEYS) break
                        val left = horizontalPaddingPx + c * (standardKeyWidth + keyGapXPx)
                        val right = left + standardKeyWidth
                        assignSlot(slotIndex++, row[c], left, top, right, bottom)
                    }
                }
                1 -> {
                    // 9 keys (A-L) centered, identical width & height to Q-P
                    val count = row.size
                    val totalRowWidth = count * standardKeyWidth + (count - 1) * keyGapXPx
                    val startX = (w - totalRowWidth) * 0.5f
                    for (c in row.indices) {
                        if (slotIndex >= MAX_KEYS) break
                        val left = startX + c * (standardKeyWidth + keyGapXPx)
                        val right = left + standardKeyWidth
                        assignSlot(slotIndex++, row[c], left, top, right, bottom)
                    }
                }
                2 -> {
                    // Shift (left) + 7 identical keys (Z-M) centered + Backspace (right)
                    val middleCount = (row.size - 2).coerceAtLeast(0)
                    val middleWidth = middleCount * standardKeyWidth + (middleCount - 1).coerceAtLeast(0) * keyGapXPx
                    val middleStartX = (w - middleWidth) * 0.5f
                    val middleEndX = middleStartX + middleWidth

                    // Left modifier (Shift / #+= / 123)
                    if (slotIndex < MAX_KEYS && row.isNotEmpty()) {
                        assignSlot(
                            slotIndex++,
                            row[0],
                            horizontalPaddingPx,
                            top,
                            (middleStartX - keyGapXPx).coerceAtLeast(horizontalPaddingPx + standardKeyWidth),
                            bottom
                        )
                    }
                    // Center keys Z-M (identical width & height)
                    for (c in 1 until row.size - 1) {
                        if (slotIndex >= MAX_KEYS) break
                        val mIdx = c - 1
                        val left = middleStartX + mIdx * (standardKeyWidth + keyGapXPx)
                        val right = left + standardKeyWidth
                        assignSlot(slotIndex++, row[c], left, top, right, bottom)
                    }
                    // Right modifier (Backspace)
                    if (slotIndex < MAX_KEYS && row.size >= 2) {
                        assignSlot(
                            slotIndex++,
                            row[row.size - 1],
                            (middleEndX + keyGapXPx).coerceAtMost(w - horizontalPaddingPx - standardKeyWidth),
                            top,
                            w - horizontalPaddingPx,
                            bottom
                        )
                    }
                }
                else -> {
                    // Bottom row: 123 | Emoji | Space | Return
                    val layerWidth = standardKeyWidth * 1.38f
                    val emojiWidth = standardKeyWidth * 1.15f
                    val returnWidth = standardKeyWidth * 2.15f
                    val spaceWidth = (usableWidth - layerWidth - emojiWidth - returnWidth - 3f * keyGapXPx)
                        .coerceAtLeast(standardKeyWidth * 2f)

                    var cursorX = horizontalPaddingPx
                    for (c in row.indices) {
                        if (slotIndex >= MAX_KEYS) break
                        val keySpec = row[c]
                        val kw = when (keySpec.kind) {
                            KeyKind.LAYER_NUMBERS, KeyKind.LAYER_LETTERS, KeyKind.LAYER_SYMBOLS -> layerWidth
                            KeyKind.EMOJI -> emojiWidth
                            KeyKind.RETURN -> returnWidth
                            KeyKind.SPACE -> spaceWidth
                            else -> standardKeyWidth
                        }
                        assignSlot(slotIndex++, keySpec, cursorX, top, cursorX + kw, bottom)
                        cursorX += kw + keyGapXPx
                    }
                }
            }
        }

        for (i in slotIndex until MAX_KEYS) {
            keySlots[i].spec = null
            keySlots[i].bounds.setEmpty()
            keySlots[i].isPressed = false
        }
        activeKeyCount = slotIndex
    }

    private fun assignSlot(
        index: Int,
        spec: KeySpec,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ) {
        val slot = keySlots[index]
        slot.spec = spec
        slot.bounds.set(left, top, right, bottom)
        slot.isPressed = false
        slot.centerX = (left + right) * 0.5f
        slot.centerY = (top + bottom) * 0.5f
        val fm = if (spec.isModifier) modFontMetrics else labelFontMetrics
        slot.textBaselineY = slot.centerY - (fm.ascent + fm.descent) * 0.5f
    }

    override fun onDraw(canvas: Canvas) {
        for (i in 0 until activeKeyCount) {
            val slot = keySlots[i]
            val spec = slot.spec ?: continue
            val pressed = slot.isPressed

            val fillColor = when {
                spec.isAccent -> if (pressed) palette.accentPressed else palette.accentFill
                spec.isModifier -> if (pressed) palette.modifierPressed else palette.modifierFill
                else -> if (pressed) palette.keyPressed else palette.keyFill
            }
            keyFillPaint.color = fillColor
            canvas.drawRoundRect(slot.bounds, keyCornerRadiusPx, keyCornerRadiusPx, keyFillPaint)

            if (spec.kind == KeyKind.SPACE && isTrackpadActive) {
                trackpadBorderPaint.color = palette.accentFill
                canvas.drawRoundRect(slot.bounds, keyCornerRadiusPx, keyCornerRadiusPx, trackpadBorderPaint)
            }

            when (spec.kind) {
                KeyKind.SHIFT -> drawShiftIcon(canvas, slot)
                KeyKind.BACKSPACE -> drawBackspaceIcon(canvas, slot)
                KeyKind.EMOJI -> drawEmojiIcon(canvas, slot)
                KeyKind.SPACE -> {
                    modLabelPaint.color = if (isTrackpadActive) palette.keyText else palette.mutedText
                    val text = if (isTrackpadActive) "‹  Trackpad  ›" else spec.displayLabel
                    canvas.drawText(
                        text,
                        slot.centerX,
                        slot.centerY - (modFontMetrics.ascent + modFontMetrics.descent) * 0.5f,
                        modLabelPaint
                    )
                }
                KeyKind.RETURN, KeyKind.LAYER_NUMBERS, KeyKind.LAYER_LETTERS, KeyKind.LAYER_SYMBOLS -> {
                    modLabelPaint.color = if (spec.isAccent) palette.textOnAccent else palette.keyText
                    canvas.drawText(spec.displayLabel, slot.centerX, slot.textBaselineY, modLabelPaint)
                }
                KeyKind.CHARACTER -> {
                    labelPaint.color = palette.keyText
                    canvas.drawText(spec.displayLabel, slot.centerX, slot.textBaselineY, labelPaint)
                }
            }
        }
    }

    private fun drawShiftIcon(canvas: Canvas, slot: KeySlot) {
        val cx = slot.centerX
        val cy = slot.centerY
        val s = density
        val active = shiftState != ShiftState.OFF
        val color = if (active) palette.textOnAccent else palette.keyText

        reusablePath.reset()
        reusablePath.moveTo(cx, cy - 8.5f * s)
        reusablePath.lineTo(cx + 8.0f * s, cy - 0.5f * s)
        reusablePath.lineTo(cx + 3.6f * s, cy - 0.5f * s)
        reusablePath.lineTo(cx + 3.6f * s, cy + 6.5f * s)
        reusablePath.lineTo(cx - 3.6f * s, cy + 6.5f * s)
        reusablePath.lineTo(cx - 3.6f * s, cy - 0.5f * s)
        reusablePath.lineTo(cx - 8.0f * s, cy - 0.5f * s)
        reusablePath.close()

        if (active) {
            iconFillPaint.color = color
            canvas.drawPath(reusablePath, iconFillPaint)
        } else {
            iconStrokePaint.color = color
            canvas.drawPath(reusablePath, iconStrokePaint)
        }

        if (shiftState == ShiftState.CAPS_LOCK) {
            iconFillPaint.color = color
            reusableRect.set(cx - 4.2f * s, cy + 8.4f * s, cx + 4.2f * s, cy + 10.4f * s)
            canvas.drawRoundRect(reusableRect, 1f * s, 1f * s, iconFillPaint)
        }
    }

    private fun drawBackspaceIcon(canvas: Canvas, slot: KeySlot) {
        val cx = slot.centerX
        val cy = slot.centerY
        val s = density
        iconStrokePaint.color = palette.keyText

        reusablePath.reset()
        reusablePath.moveTo(cx - 9.5f * s, cy)
        reusablePath.lineTo(cx - 4.0f * s, cy - 6.5f * s)
        reusablePath.lineTo(cx + 8.5f * s, cy - 6.5f * s)
        reusablePath.lineTo(cx + 8.5f * s, cy + 6.5f * s)
        reusablePath.lineTo(cx - 4.0f * s, cy + 6.5f * s)
        reusablePath.close()
        canvas.drawPath(reusablePath, iconStrokePaint)

        // X inside backspace
        canvas.drawLine(cx - 0.8f * s, cy - 2.6f * s, cx + 4.6f * s, cy + 2.6f * s, iconStrokePaint)
        canvas.drawLine(cx + 4.6f * s, cy - 2.6f * s, cx - 0.8f * s, cy + 2.6f * s, iconStrokePaint)
    }

    private fun drawEmojiIcon(canvas: Canvas, slot: KeySlot) {
        val cx = slot.centerX
        val cy = slot.centerY
        val s = density
        val tint = palette.emojiAccent
        iconStrokePaint.color = tint
        iconFillPaint.color = tint

        canvas.drawCircle(cx, cy, 8.2f * s, iconStrokePaint)
        canvas.drawCircle(cx - 2.7f * s, cy - 1.8f * s, 1.15f * s, iconFillPaint)
        canvas.drawCircle(cx + 2.7f * s, cy - 1.8f * s, 1.15f * s, iconFillPaint)
        reusableRect.set(cx - 4.0f * s, cy - 2.2f * s, cx + 4.0f * s, cy + 4.5f * s)
        canvas.drawArc(reusableRect, 20f, 140f, false, iconStrokePaint)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        val nowMs = SystemClock.uptimeMillis()

        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val actionIdx = event.actionIndex
                val pid = event.getPointerId(actionIdx)
                val x = event.getX(actionIdx)
                val y = event.getY(actionIdx)
                val hitIdx = hitTestKey(x, y)

                if (hitIdx >= 0) {
                    val hitSpec = keySlots[hitIdx].spec
                    // N-key rollover: commit any older uncommitted character key immediately so order is preserved
                    if (hitSpec != null && hitSpec.kind == KeyKind.CHARACTER) {
                        commitPendingCharacterPointersBefore(nowMs)
                    }
                    val pSlot = allocatePointerSlot(pid)
                    if (pSlot >= 0) {
                        pointerKeyIdx[pSlot] = hitIdx
                        pointerDownX[pSlot] = x
                        pointerDownY[pSlot] = y
                        pointerLastX[pSlot] = x
                        pointerLastY[pSlot] = y
                        pointerAccumX[pSlot] = 0f
                        pointerAccumY[pSlot] = 0f
                        pointerDownTime[pSlot] = nowMs
                        pointerCommitted[pSlot] = false
                        pointerTrackpad[pSlot] = false
                        pointerSwipeDelete[pSlot] = false

                        if (hitSpec != null) {
                            listener?.onKeyPressedDown(hitSpec.kind)
                            when (hitSpec.kind) {
                                KeyKind.SPACE -> {
                                    activeSpacePointerSlot = pSlot
                                    removeCallbacks(spaceHoldRunnable)
                                    postDelayed(spaceHoldRunnable, SPACE_HOLD_TRACKPAD_MS)
                                }
                                KeyKind.BACKSPACE -> {
                                    activeBackspacePointerSlot = pSlot
                                    removeCallbacks(backspaceHoldRunnable)
                                    postDelayed(backspaceHoldRunnable, BACKSPACE_HOLD_MS)
                                }
                                KeyKind.SHIFT -> {
                                    activeShiftPointerSlot = pSlot
                                    removeCallbacks(shiftLongPressRunnable)
                                    postDelayed(shiftLongPressRunnable, SHIFT_LONG_PRESS_MS)
                                }
                                else -> Unit
                            }
                        }
                        syncPressedKeyStates()
                        invalidate()
                    }
                }
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                var needsRedraw = false
                for (i in 0 until event.pointerCount) {
                    val pid = event.getPointerId(i)
                    val pSlot = findPointerSlot(pid)
                    if (pSlot < 0) continue

                    val keyIdx = pointerKeyIdx[pSlot]
                    if (keyIdx < 0 || keyIdx >= activeKeyCount) continue
                    val spec = keySlots[keyIdx].spec ?: continue

                    val x = event.getX(i)
                    val y = event.getY(i)
                    val dx = x - pointerLastX[pSlot]
                    val dy = y - pointerLastY[pSlot]
                    pointerLastX[pSlot] = x
                    pointerLastY[pSlot] = y

                    if (spec.kind == KeyKind.SPACE) {
                        val totalDx = x - pointerDownX[pSlot]
                        val totalDy = y - pointerDownY[pSlot]
                        if (!pointerTrackpad[pSlot] && hypot(totalDx, totalDy) > trackpadDragThresholdPx) {
                            removeCallbacks(spaceHoldRunnable)
                            pointerTrackpad[pSlot] = true
                            pointerCommitted[pSlot] = true
                            pointerAccumX[pSlot] = 0f
                            pointerAccumY[pSlot] = 0f
                            isTrackpadActive = true
                            needsRedraw = true
                        }
                        if (pointerTrackpad[pSlot]) {
                            pointerAccumX[pSlot] += dx
                            pointerAccumY[pSlot] += dy

                            var stepsX = 0
                            while (pointerAccumX[pSlot] >= trackpadStepXPx) {
                                pointerAccumX[pSlot] -= trackpadStepXPx
                                stepsX++
                            }
                            while (pointerAccumX[pSlot] <= -trackpadStepXPx) {
                                pointerAccumX[pSlot] += trackpadStepXPx
                                stepsX--
                            }
                            if (stepsX != 0) {
                                listener?.onTrackpadStepHorizontal(stepsX)
                            }

                            var stepsY = 0
                            while (pointerAccumY[pSlot] >= trackpadStepYPx) {
                                pointerAccumY[pSlot] -= trackpadStepYPx
                                stepsY++
                            }
                            while (pointerAccumY[pSlot] <= -trackpadStepYPx) {
                                pointerAccumY[pSlot] += trackpadStepYPx
                                stepsY--
                            }
                            if (stepsY != 0) {
                                listener?.onTrackpadStepVertical(stepsY)
                            }
                        }
                    } else if (spec.kind == KeyKind.BACKSPACE) {
                        val totalDx = x - pointerDownX[pSlot]
                        if (!pointerSwipeDelete[pSlot] && totalDx < -backspaceSwipeThresholdPx) {
                            removeCallbacks(backspaceHoldRunnable)
                            pointerSwipeDelete[pSlot] = true
                            pointerCommitted[pSlot] = true
                            pointerAccumX[pSlot] = 0f
                            listener?.onDeleteWord()
                        } else if (pointerSwipeDelete[pSlot]) {
                            pointerAccumX[pSlot] += dx
                            while (pointerAccumX[pSlot] <= -backspaceRepeatStepPx) {
                                pointerAccumX[pSlot] += backspaceRepeatStepPx
                                listener?.onDeleteWord()
                            }
                        }
                    } else if (!pointerCommitted[pSlot] && spec.kind == KeyKind.CHARACTER) {
                        // Allow minor finger roll to update target key only if not sliding far
                        val movedIdx = hitTestKey(x, y)
                        if (movedIdx >= 0 && movedIdx != keyIdx) {
                            val movedSpec = keySlots[movedIdx].spec
                            if (movedSpec != null && movedSpec.kind == KeyKind.CHARACTER) {
                                pointerKeyIdx[pSlot] = movedIdx
                                needsRedraw = true
                            }
                        }
                    }
                }
                if (needsRedraw) {
                    syncPressedKeyStates()
                    invalidate()
                }
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val actionIdx = event.actionIndex
                val pid = event.getPointerId(actionIdx)
                val pSlot = findPointerSlot(pid)
                if (pSlot >= 0) {
                    finishPointerSlot(pSlot, nowMs, cancelled = false)
                }
                if (action == MotionEvent.ACTION_UP) {
                    performClick()
                }
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                for (s in 0 until MAX_POINTERS) {
                    if (pointerIds[s] != INVALID_POINTER) {
                        finishPointerSlot(s, nowMs, cancelled = true)
                    }
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun commitPendingCharacterPointersBefore(nowMs: Long) {
        // Commit older uncommitted CHARACTER pointers in chronological order
        while (true) {
            var oldestSlot = -1
            var oldestTime = Long.MAX_VALUE
            for (s in 0 until MAX_POINTERS) {
                if (pointerIds[s] != INVALID_POINTER && !pointerCommitted[s]) {
                    val idx = pointerKeyIdx[s]
                    val spec = if (idx in 0 until activeKeyCount) keySlots[idx].spec else null
                    if (spec != null && spec.kind == KeyKind.CHARACTER && pointerDownTime[s] < oldestTime) {
                        oldestTime = pointerDownTime[s]
                        oldestSlot = s
                    }
                }
            }
            if (oldestSlot < 0) break
            val idx = pointerKeyIdx[oldestSlot]
            val spec = keySlots[idx].spec
            if (spec != null) {
                val chordShift = isShiftPointerHeld()
                listener?.onCharacterKey(spec.primaryChar, chordShift)
                pointerCommitted[oldestSlot] = true
            } else {
                break
            }
        }
    }

    private fun finishPointerSlot(pSlot: Int, nowMs: Long, cancelled: Boolean) {
        val keyIdx = pointerKeyIdx[pSlot]
        val spec = if (keyIdx in 0 until activeKeyCount) keySlots[keyIdx].spec else null

        if (pSlot == activeSpacePointerSlot) {
            removeCallbacks(spaceHoldRunnable)
            activeSpacePointerSlot = -1
            if (pointerTrackpad[pSlot]) {
                isTrackpadActive = false
            }
        }
        if (pSlot == activeBackspacePointerSlot) {
            removeCallbacks(backspaceHoldRunnable)
            activeBackspacePointerSlot = -1
        }
        if (pSlot == activeShiftPointerSlot) {
            removeCallbacks(shiftLongPressRunnable)
            activeShiftPointerSlot = -1
        }

        if (!cancelled && spec != null && !pointerCommitted[pSlot]) {
            when (spec.kind) {
                KeyKind.CHARACTER -> {
                    val chordShift = isShiftPointerHeld()
                    listener?.onCharacterKey(spec.primaryChar, chordShift)
                }
                KeyKind.SHIFT -> {
                    // If Shift was held while another key was typed, release chord; else tap Shift
                    listener?.onShiftTap(nowMs)
                }
                KeyKind.BACKSPACE -> listener?.onBackspaceTap()
                KeyKind.SPACE -> listener?.onSpaceTap(nowMs)
                KeyKind.LAYER_NUMBERS -> listener?.onLayerSwitch(KeyboardLayer.NUMBERS)
                KeyKind.LAYER_LETTERS -> listener?.onLayerSwitch(KeyboardLayer.LETTERS)
                KeyKind.LAYER_SYMBOLS -> listener?.onLayerSwitch(KeyboardLayer.SYMBOLS)
                KeyKind.EMOJI -> listener?.onEmojiKey()
                KeyKind.RETURN -> listener?.onReturnKey()
            }
        } else if (!cancelled && spec != null && spec.kind == KeyKind.SHIFT) {
            listener?.onShiftChordReleased()
        }

        pointerIds[pSlot] = INVALID_POINTER
        pointerKeyIdx[pSlot] = -1
        pointerCommitted[pSlot] = false
        pointerTrackpad[pSlot] = false
        pointerSwipeDelete[pSlot] = false

        syncPressedKeyStates()
        invalidate()
    }

    private fun isShiftPointerHeld(): Boolean {
        val s = activeShiftPointerSlot
        if (s < 0 || pointerIds[s] == INVALID_POINTER) return false
        pointerCommitted[s] = true // mark chord used so releasing Shift won't double-toggle
        return true
    }

    private fun syncPressedKeyStates() {
        for (i in 0 until activeKeyCount) {
            keySlots[i].isPressed = false
        }
        for (s in 0 until MAX_POINTERS) {
            if (pointerIds[s] != INVALID_POINTER) {
                val idx = pointerKeyIdx[s]
                if (idx in 0 until activeKeyCount) {
                    keySlots[idx].isPressed = true
                }
            }
        }
    }

    private fun hitTestKey(x: Float, y: Float): Int {
        for (i in 0 until activeKeyCount) {
            if (keySlots[i].bounds.contains(x, y)) {
                return i
            }
        }
        // Fallback nearest key within gap tolerance so fast typing between gaps never drops
        var bestIdx = -1
        var bestDistSq = Float.MAX_VALUE
        val maxGapSq = (keyGapXPx + keyGapYPx) * (keyGapXPx + keyGapYPx)
        for (i in 0 until activeKeyCount) {
            val b = keySlots[i].bounds
            val cx = x.coerceIn(b.left, b.right)
            val cy = y.coerceIn(b.top, b.bottom)
            val dx = x - cx
            val dy = y - cy
            val distSq = dx * dx + dy * dy
            if (distSq < bestDistSq && distSq <= maxGapSq) {
                bestDistSq = distSq
                bestIdx = i
            }
        }
        return bestIdx
    }

    private fun allocatePointerSlot(pid: Int): Int {
        val existing = findPointerSlot(pid)
        if (existing >= 0) return existing
        for (i in 0 until MAX_POINTERS) {
            if (pointerIds[i] == INVALID_POINTER) {
                pointerIds[i] = pid
                return i
            }
        }
        return -1
    }

    private fun findPointerSlot(pid: Int): Int {
        for (i in 0 until MAX_POINTERS) {
            if (pointerIds[i] == pid) return i
        }
        return -1
    }

    internal fun findKeyCenter(keyId: String): Pair<Float, Float>? {
        for (i in 0 until activeKeyCount) {
            val slot = keySlots[i]
            if (slot.spec?.id == keyId && !slot.bounds.isEmpty) {
                return Pair(slot.centerX, slot.centerY)
            }
        }
        return null
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(spaceHoldRunnable)
        removeCallbacks(backspaceHoldRunnable)
        removeCallbacks(shiftLongPressRunnable)
        super.onDetachedFromWindow()
    }

    companion object {
        private const val MAX_KEYS = 48
        private const val MAX_POINTERS = 10
        private const val INVALID_POINTER = -1
        private const val SPACE_HOLD_TRACKPAD_MS = 180L
        private const val BACKSPACE_HOLD_MS = 350L
        private const val BACKSPACE_REPEAT_MS = 120L
        private const val SHIFT_LONG_PRESS_MS = 450L
    }
}
