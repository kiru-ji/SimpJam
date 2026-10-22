package com.maxrave.simpmusic.expect.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Renders [text] as a QR code. Dark modules on white regardless of theme — scanners expect that
 * polarity. zxing lives in the platform source sets (android/jvm), so the encoding is expect/actual
 * while every call site stays in common.
 */
@Composable
expect fun QrCodeImage(
    text: String,
    modifier: Modifier = Modifier,
)
