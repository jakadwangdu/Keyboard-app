package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.model.AttachmentType
import com.example.model.IconPackType
import com.example.model.KeyboardMode
import com.example.model.SwitchType
import com.example.viewmodel.KeyboardViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class KeyboardUnitTest {

  private lateinit var viewModel: KeyboardViewModel

  @Before
  fun setup() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    viewModel = KeyboardViewModel(application)
  }

  @Test
  fun testTypingAndBackspace() {
    viewModel.typeKey("H")
    viewModel.typeKey("e")
    viewModel.typeKey("l")
    viewModel.typeKey("l")
    viewModel.typeKey("o")

    assertEquals("Hello", viewModel.activeText.value)

    viewModel.backspace()
    assertEquals("Hell", viewModel.activeText.value)
  }

  @Test
  fun testSendMessage() {
    val initialCount = viewModel.messages.value.size
    viewModel.typeKey("Testing mechanical switch audio")
    viewModel.sendMessage()

    assertEquals("", viewModel.activeText.value)
    assertEquals(initialCount + 1, viewModel.messages.value.size)
    assertEquals("Testing mechanical switch audio", viewModel.messages.value.last().text)
  }

  @Test
  fun testSendAttachment() {
    val initialCount = viewModel.messages.value.size
    viewModel.sendAttachment(AttachmentType.PHOTO)

    assertEquals(initialCount + 1, viewModel.messages.value.size)
    assertEquals(AttachmentType.PHOTO, viewModel.messages.value.last().attachmentType)
  }

  @Test
  fun testTextFormatting() {
    viewModel.typeKey("boldText")
    viewModel.formatText("*")
    assertEquals("*boldText*", viewModel.activeText.value)
  }

  @Test
  fun testSwitchAndIconPackSelection() {
    viewModel.setSwitch(SwitchType.MODEL_M_SPRING)
    assertEquals(SwitchType.MODEL_M_SPRING, viewModel.currentSwitch.value)

    viewModel.setIconPack(IconPackType.WHATSAPP_EXPRESSIVE)
    assertEquals(IconPackType.WHATSAPP_EXPRESSIVE, viewModel.iconPackType.value)

    viewModel.setMode(KeyboardMode.EMOJI_DRAWER)
    assertEquals(KeyboardMode.EMOJI_DRAWER, viewModel.keyboardMode.value)
  }

  @Test
  fun testTypingNewEmojis() {
    viewModel.typeKey("🫠")
    viewModel.typeKey("😶‍🌫️")
    assertEquals("🫠😶‍🌫️", viewModel.activeText.value)
  }
}
