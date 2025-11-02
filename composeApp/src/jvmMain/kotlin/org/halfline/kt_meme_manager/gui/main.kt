package org.halfline.kt_meme_manager.gui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*

fun main() = application {
    val windowState = rememberWindowState(width = 992.dp, height = 768.dp)
    var isMainWindowVisible by remember { mutableStateOf(true) }

    // 只有在支持系统托盘的平台上才创建托盘
    if (isTraySupported) {
        Tray(
            state = rememberTrayState(), icon = TrayIcon, menu = {
                Item("Show/Hide", onClick = {
                    isMainWindowVisible = !isMainWindowVisible
                })
                Separator()
                Item("Exit", onClick = ::exitApplication)
            }
        )
    }

    Window(
        onCloseRequest = {
            if (isTraySupported) {
                isMainWindowVisible = false
            } else {
                exitApplication()
            }
        },
        title = "Kotlin Meme Manager",
        state = windowState,
        visible = isMainWindowVisible,
        resizable = false
    ) {
        MaterialTheme(
            colorScheme = darkColorScheme()
        ) {
            Surface(
                color = MaterialTheme.colorScheme.background, contentColor = MaterialTheme.colorScheme.onBackground
            ) {
                App()
            }
        }
    }
}

object TrayIcon : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {
        drawCircle(Color(0xFFFFA500))
    }
}