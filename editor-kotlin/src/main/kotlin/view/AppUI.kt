package view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray

private val message: MutableState<String> = mutableStateOf("")
private val state: MutableState<Boolean> = mutableStateOf(false)

@Composable
fun appUI(text: String) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Gray
    ) {
        Text(text)
    }
}