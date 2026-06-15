package com.prorf.app.ui.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Returns a callback that shows a short toast. Used by prototype buttons whose
 * underlying logic is not yet implemented, so interactions still give feedback.
 */
@Composable
fun rememberToast(): (String) -> Unit {
    val ctx = LocalContext.current
    return { msg -> Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show() }
}
