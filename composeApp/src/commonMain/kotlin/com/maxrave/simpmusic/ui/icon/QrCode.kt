package com.maxrave.simpmusic.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * A QR glyph: the three finder patterns (nested squares, even-odd fill punches the ring) plus a
 * scatter of data modules. Drawn by hand for the Listen Together invite dialog — SimpJam.
 */
@Suppress("CheckReturnValue")
val SimpIcons.QrCode: ImageVector
  get() {
    if (_QrCode != null) {
      return _QrCode!!
    }
    _QrCode =
      ImageVector.Builder(
          name = "QrCode",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f,
        )
        .apply {
          path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.Companion.EvenOdd,
          ) {
            // Top-left finder
            moveTo(3f, 3f); lineTo(10f, 3f); lineTo(10f, 10f); lineTo(3f, 10f); close()
            moveTo(4.5f, 4.5f); lineTo(8.5f, 4.5f); lineTo(8.5f, 8.5f); lineTo(4.5f, 8.5f); close()
            moveTo(5.75f, 5.75f); lineTo(7.25f, 5.75f); lineTo(7.25f, 7.25f); lineTo(5.75f, 7.25f); close()
            // Top-right finder
            moveTo(14f, 3f); lineTo(21f, 3f); lineTo(21f, 10f); lineTo(14f, 10f); close()
            moveTo(15.5f, 4.5f); lineTo(19.5f, 4.5f); lineTo(19.5f, 8.5f); lineTo(15.5f, 8.5f); close()
            moveTo(16.75f, 5.75f); lineTo(18.25f, 5.75f); lineTo(18.25f, 7.25f); lineTo(16.75f, 7.25f); close()
            // Bottom-left finder
            moveTo(3f, 14f); lineTo(10f, 14f); lineTo(10f, 21f); lineTo(3f, 21f); close()
            moveTo(4.5f, 15.5f); lineTo(8.5f, 15.5f); lineTo(8.5f, 19.5f); lineTo(4.5f, 19.5f); close()
            moveTo(5.75f, 16.75f); lineTo(7.25f, 16.75f); lineTo(7.25f, 18.25f); lineTo(5.75f, 18.25f); close()
            // Data modules
            moveTo(14f, 14f); lineTo(16.25f, 14f); lineTo(16.25f, 16.25f); lineTo(14f, 16.25f); close()
            moveTo(17.75f, 14f); lineTo(20f, 14f); lineTo(20f, 16.25f); lineTo(17.75f, 16.25f); close()
            moveTo(14f, 17.75f); lineTo(16.25f, 17.75f); lineTo(16.25f, 20f); lineTo(14f, 20f); close()
            moveTo(17.75f, 17.75f); lineTo(21f, 17.75f); lineTo(21f, 21f); lineTo(17.75f, 21f); close()
          }
        }
        .buildSimpIcon()
    return _QrCode!!
  }

private var _QrCode: ImageVector? = null
