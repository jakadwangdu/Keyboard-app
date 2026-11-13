# Aura Keyboard

Native Android system-wide keyboard (`InputMethodService`) built in Kotlin with a single Canvas-drawn custom `KeyboardView`, warm espresso gradient surface, cream highlights, and zero-allocation draw/touch loops.

## Features

- **Custom Canvas IME (`KeyboardView`)**: Draws all keys on a single `Canvas` with reused `Paint`, `Path`, and `RectF` objects and zero allocations in `onDraw()` or `onTouchEvent()`.
- **Multi-Touch Rollover**: Tracks active pointers via fixed-size primitive arrays (`MAX_POINTERS = 10`) so fast alternating-thumb typing commits keys in strict chronological order without dropped characters.
- **Warm Espresso & Cream Design**: 34dp rounded top corners (`AuraCardLayout`), `#211E1B` to `#171513` vertical gradient in dark mode, warm `#EFE7DA` accents, plus Light Cream and Auto system theme modes.
- **Layouts & Layers**: Q-P / A-L / Z-M QWERTY rows with uniform key dimensions, `123` Numbers and `#+=` Symbols layers (including the `₹` rupee sign), dedicated 4-column numeric keypad for number/phone/date editors, Shift, and Caps Lock.
- **Smart Gestures**:
  - **Spacebar**: Tap for space; double-tap converts trailing word space to `". "`; hold `180 ms` or drag `> 6dp` to switch into cursor trackpad mode (`9dp` horizontal per caret step, `22dp` vertical per line).
  - **Backspace**: Grapheme/emoji-safe single-character delete on tap; hold (`350 ms` repeat) or swipe left (`> 16dp`) to delete whole words.
- **Toolbar & Panels**: Clipboard history (last 20 clips with pinning in `SharedPreferences`, skipping password fields and sensitive clips), Emoji panel with categories and persisted Recents row, key height preset (`14` / `16` / `18`), and Voice Typing (`RecognizerIntent`).
- **Tactile & Audio Feedback**: Preloaded WAV clicks via `SoundPool` and `VibrationEffect` haptics, both toggleable in settings.
- **Privacy-First**: Requests no `INTERNET` permission. Microphone (`RECORD_AUDIO`) permission is requested only if you tap Voice Typing.

## Build & Install

Requirements: JDK 17 and Android SDK (`minSdk 26`, `targetSdk 35`).

```bash
gradle :app:assembleDebug
gradle :app:testDebugUnitTest
gradle :app:lint
```

Install the debug APK on a connected device or emulator:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Enable & Select Aura Keyboard

1. Open **Aura Keyboard** from the launcher.
2. Tap **Open Keyboard Settings** and toggle **Aura Keyboard** on in Android's *On-screen keyboard* settings.
3. Return to the app and tap **Choose Keyboard** to switch your active input method to **Aura Keyboard**.
4. Tap the **Test Input Field** in the settings screen to verify typing, cursor trackpad, clipboard history, emoji panel, and size presets.

## Known Limits

- **Voice Typing**: Relies on an installed system speech recognition provider supporting `RecognizerIntent.ACTION_RECOGNIZE_SPEECH`. If no speech recognizer service is available on the device, Aura Keyboard shows a brief toast and stays responsive.
- **Layout Scope**: Built with a US-English QWERTY layout plus currency symbols (`₹`, `$`, `€`, `£`, `¥`). Hardware physical keyboard keymap overrides and multi-language autocorrect dictionaries are not included.
- **Autocorrect**: Supports `EditorInfo` auto-capitalization (sentences/words/all-caps), IME action labels (`Send`, `Search`, `Go`, `Next`, `Done`, `Return`), and password/numeric input modes without dictionary spell-checking or cloud telemetry.
