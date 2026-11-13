package app.aura.keyboard.model

object KeyboardLayoutBuilder {

    private val lettersRow0 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    private val lettersRow1 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    private val lettersRow2 = listOf("z", "x", "c", "v", "b", "n", "m")

    private val numbersRow0 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    private val numbersRow1 = listOf("-", "/", ":", ";", "(", ")", "₹", "&", "@")
    private val numbersRow2 = listOf(".", ",", "?", "!", "'", "\"", "_")

    private val symbolsRow0 = listOf("[", "]", "{", "}", "#", "%", "^", "*", "+", "=")
    private val symbolsRow1 = listOf("_", "\\", "|", "~", "<", ">", "€", "£", "¥")
    private val symbolsRow2 = listOf(".", ",", "?", "!", "₹", "°", "•")

    fun buildRows(
        layer: KeyboardLayer,
        shiftState: ShiftState,
        imeActionKind: ImeActionKind
    ): List<List<KeySpec>> {
        return when (layer) {
            KeyboardLayer.LETTERS -> buildLettersRows(shiftState, imeActionKind)
            KeyboardLayer.NUMBERS -> buildNumbersRows(imeActionKind)
            KeyboardLayer.SYMBOLS -> buildSymbolsRows(imeActionKind)
            KeyboardLayer.NUMBER_PAD -> buildNumberPadRows(imeActionKind)
        }
    }

    private fun buildLettersRows(
        shiftState: ShiftState,
        imeActionKind: ImeActionKind
    ): List<List<KeySpec>> {
        val upper = shiftState != ShiftState.OFF
        val row0 = lettersRow0.map { ch ->
            val label = if (upper) ch.uppercase() else ch
            KeySpec(
                id = "char_$ch",
                kind = KeyKind.CHARACTER,
                primaryChar = ch,
                shiftedChar = ch.uppercase(),
                displayLabel = label,
                row = 0
            )
        }
        val row1 = lettersRow1.map { ch ->
            val label = if (upper) ch.uppercase() else ch
            KeySpec(
                id = "char_$ch",
                kind = KeyKind.CHARACTER,
                primaryChar = ch,
                shiftedChar = ch.uppercase(),
                displayLabel = label,
                row = 1
            )
        }
        val row2 = buildList {
            add(
                KeySpec(
                    id = "shift",
                    kind = KeyKind.SHIFT,
                    displayLabel = "",
                    row = 2,
                    isModifier = true,
                    isAccent = shiftState != ShiftState.OFF
                )
            )
            lettersRow2.forEach { ch ->
                val label = if (upper) ch.uppercase() else ch
                add(
                    KeySpec(
                        id = "char_$ch",
                        kind = KeyKind.CHARACTER,
                        primaryChar = ch,
                        shiftedChar = ch.uppercase(),
                        displayLabel = label,
                        row = 2
                    )
                )
            }
            add(
                KeySpec(
                    id = "backspace",
                    kind = KeyKind.BACKSPACE,
                    displayLabel = "",
                    row = 2,
                    isModifier = true
                )
            )
        }
        val row3 = buildBottomRow(
            layerToggleKind = KeyKind.LAYER_NUMBERS,
            layerToggleLabel = "123",
            imeActionKind = imeActionKind
        )
        return listOf(row0, row1, row2, row3)
    }

    private fun buildNumbersRows(imeActionKind: ImeActionKind): List<List<KeySpec>> {
        val row0 = numbersRow0.map { ch ->
            KeySpec(
                id = "num_$ch",
                kind = KeyKind.CHARACTER,
                primaryChar = ch,
                displayLabel = ch,
                row = 0
            )
        }
        val row1 = numbersRow1.map { ch ->
            KeySpec(
                id = "num_$ch",
                kind = KeyKind.CHARACTER,
                primaryChar = ch,
                displayLabel = ch,
                row = 1
            )
        }
        val row2 = buildList {
            add(
                KeySpec(
                    id = "to_symbols",
                    kind = KeyKind.LAYER_SYMBOLS,
                    displayLabel = "#+=",
                    row = 2,
                    isModifier = true
                )
            )
            numbersRow2.forEach { ch ->
                KeySpec(
                    id = "num_$ch",
                    kind = KeyKind.CHARACTER,
                    primaryChar = ch,
                    displayLabel = ch,
                    row = 2
                ).also { add(it) }
            }
            add(
                KeySpec(
                    id = "backspace",
                    kind = KeyKind.BACKSPACE,
                    displayLabel = "",
                    row = 2,
                    isModifier = true
                )
            )
        }
        val row3 = buildBottomRow(
            layerToggleKind = KeyKind.LAYER_LETTERS,
            layerToggleLabel = "ABC",
            imeActionKind = imeActionKind
        )
        return listOf(row0, row1, row2, row3)
    }

    private fun buildSymbolsRows(imeActionKind: ImeActionKind): List<List<KeySpec>> {
        val row0 = symbolsRow0.map { ch ->
            KeySpec(
                id = "sym_$ch",
                kind = KeyKind.CHARACTER,
                primaryChar = ch,
                displayLabel = ch,
                row = 0
            )
        }
        val row1 = symbolsRow1.map { ch ->
            KeySpec(
                id = "sym_$ch",
                kind = KeyKind.CHARACTER,
                primaryChar = ch,
                displayLabel = ch,
                row = 1
            )
        }
        val row2 = buildList {
            add(
                KeySpec(
                    id = "to_numbers",
                    kind = KeyKind.LAYER_NUMBERS,
                    displayLabel = "123",
                    row = 2,
                    isModifier = true
                )
            )
            symbolsRow2.forEach { ch ->
                add(
                    KeySpec(
                        id = "sym_$ch",
                        kind = KeyKind.CHARACTER,
                        primaryChar = ch,
                        displayLabel = ch,
                        row = 2
                    )
                )
            }
            add(
                KeySpec(
                    id = "backspace",
                    kind = KeyKind.BACKSPACE,
                    displayLabel = "",
                    row = 2,
                    isModifier = true
                )
            )
        }
        val row3 = buildBottomRow(
            layerToggleKind = KeyKind.LAYER_LETTERS,
            layerToggleLabel = "ABC",
            imeActionKind = imeActionKind
        )
        return listOf(row0, row1, row2, row3)
    }

    private fun buildNumberPadRows(imeActionKind: ImeActionKind): List<List<KeySpec>> {
        fun numKey(ch: String, row: Int, isMod: Boolean = false) = KeySpec(
            id = "pad_$ch",
            kind = KeyKind.CHARACTER,
            primaryChar = ch,
            displayLabel = ch,
            row = row,
            isModifier = isMod
        )

        val row0 = listOf(numKey("1", 0), numKey("2", 0), numKey("3", 0), numKey("-", 0, true))
        val row1 = listOf(numKey("4", 1), numKey("5", 1), numKey("6", 1), numKey(".", 1, true))
        val row2 = listOf(
            numKey("7", 2),
            numKey("8", 2),
            numKey("9", 2),
            KeySpec(id = "backspace", kind = KeyKind.BACKSPACE, displayLabel = "", row = 2, isModifier = true)
        )
        val row3 = listOf(
            KeySpec(
                id = "to_numbers_from_pad",
                kind = KeyKind.LAYER_NUMBERS,
                displayLabel = "*#+",
                row = 3,
                isModifier = true
            ),
            numKey("0", 3),
            KeySpec(id = "space", kind = KeyKind.SPACE, primaryChar = " ", displayLabel = "Space", row = 3),
            KeySpec(
                id = "return",
                kind = KeyKind.RETURN,
                displayLabel = imeActionKind.defaultLabel,
                row = 3,
                isModifier = true,
                isAccent = imeActionKind != ImeActionKind.RETURN
            )
        )
        return listOf(row0, row1, row2, row3)
    }

    private fun buildBottomRow(
        layerToggleKind: KeyKind,
        layerToggleLabel: String,
        imeActionKind: ImeActionKind
    ): List<KeySpec> {
        return listOf(
            KeySpec(
                id = "layer_toggle",
                kind = layerToggleKind,
                displayLabel = layerToggleLabel,
                row = 3,
                isModifier = true
            ),
            KeySpec(
                id = "emoji",
                kind = KeyKind.EMOJI,
                displayLabel = "",
                row = 3,
                isModifier = true
            ),
            KeySpec(
                id = "space",
                kind = KeyKind.SPACE,
                primaryChar = " ",
                displayLabel = "Space",
                row = 3
            ),
            KeySpec(
                id = "return",
                kind = KeyKind.RETURN,
                displayLabel = imeActionKind.defaultLabel,
                row = 3,
                isModifier = true,
                isAccent = imeActionKind != ImeActionKind.RETURN
            )
        )
    }
}
