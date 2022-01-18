import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.*
import model.LoadingState
import style.icAppRounded
import view.SplashUI
import view.appUI

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {

    val icon = icAppRounded()

    val content = remember {
        LoadingState
    }

    var action by remember { mutableStateOf("Last action: None") }
    var isOpen by remember { mutableStateOf(true) }
    var isSubmenuShowing by remember { mutableStateOf(false) }

    if (content.isAppReady()) {
        Window(
            onCloseRequest = ::exitApplication,
            title = "云编辑器",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
            ),
            icon = icon,
        ) {
            MaterialTheme {
                MenuBar {
                    Menu("File", mnemonic = 'F') {
                        Item("Copy", onClick = { action = "Last action: Copy" }, shortcut = KeyShortcut(Key.C, ctrl = true))
                        Item("Paste", onClick = { action = "Last action: Paste" }, shortcut = KeyShortcut(Key.V, ctrl = true))
                    }
                    Menu("Actions", mnemonic = 'A') {
                        CheckboxItem(
                            "Advanced settings",
                            checked = isSubmenuShowing,
                            onCheckedChange = {
                                isSubmenuShowing = !isSubmenuShowing
                            }
                        )
                        if (isSubmenuShowing) {
                            Menu("Settings") {
                                Item("Setting 1", onClick = { action = "Last action: Setting 1" })
                                Item("Setting 2", onClick = { action = "Last action: Setting 2" })
                            }
                        }
                        Separator()
                        Item("About", icon = TrayIcon, onClick = { action = "Last action: About" })
                        Item("Exit", onClick = { isOpen = false }, shortcut = KeyShortcut(Key.Escape), mnemonic = 'E')
                    }
                }

                appUI(action)
            }
        }
    } else {
        Window(
            onCloseRequest = ::exitApplication,
            title = "云编辑器",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
            ),
            undecorated = true,
            icon = icon,
        ) {
            MaterialTheme {
                SplashUI()
            }
        }
    }
}

object TrayIcon : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {
        drawOval(Color(0xFFFFA500))
    }
}