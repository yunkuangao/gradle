package model

import androidx.compose.runtime.mutableStateOf
import java.util.*
import kotlin.concurrent.schedule

object LoadingState {
    private val isAppReady = mutableStateOf(false)
    fun isAppReady(): Boolean {
        if (!isAppReady.value) {
            Timer("lateTrue", false).schedule(3 * 1000) {
                isAppReady.value = true
            }
        }
        return isAppReady.value
    }
}