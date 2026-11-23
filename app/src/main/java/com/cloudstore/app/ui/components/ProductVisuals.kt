package com.cloudstore.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.Dns
import androidx.compose.material.icons.rounded.Earbuds
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.HeadsetMic
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Monitor
import androidx.compose.material.icons.rounded.Mouse
import androidx.compose.material.icons.rounded.SdStorage
import androidx.compose.material.icons.rounded.Speaker
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.VideogameAsset
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cloudstore.app.model.Category
import com.cloudstore.app.model.Product
import java.text.NumberFormat
import java.util.Locale

private val mxnLocale = Locale.forLanguageTag("es-MX")

fun formatCurrencyMXN(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(mxnLocale).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return "$${formatter.format(amount)} MXN"
}

fun productIconFor(iconName: String): ImageVector = when (iconName) {
    "Headphones" -> Icons.Rounded.Headphones
    "Earbuds" -> Icons.Rounded.Earbuds
    "Speaker" -> Icons.Rounded.Speaker
    "Keyboard" -> Icons.Rounded.Keyboard
    "Mouse" -> Icons.Rounded.Mouse
    "Monitor" -> Icons.Rounded.Monitor
    "SportsEsports" -> Icons.Rounded.SportsEsports
    "VideogameAsset" -> Icons.Rounded.VideogameAsset
    "HeadsetMic" -> Icons.Rounded.HeadsetMic
    "Memory" -> Icons.Rounded.Memory
    "SdStorage" -> Icons.Rounded.SdStorage
    "Dns" -> Icons.Rounded.Dns
    else -> Icons.Rounded.Devices
}

fun categoryGradientColors(category: Category): List<Color> = when (category) {
    Category.AUDIO -> listOf(Color(0xFF4F46E5), Color(0xFF9333EA))
    Category.PERIPHERALS -> listOf(Color(0xFF2563EB), Color(0xFF0891B2))
    Category.GAMING -> listOf(Color(0xFFE11D48), Color(0xFFEA580C))
    Category.STORAGE -> listOf(Color(0xFF059669), Color(0xFF0D9488))
    Category.ALL -> listOf(Color(0xFF334155), Color(0xFF4F46E5))
}

fun categoryGradientBrush(category: Category): Brush =
    Brush.linearGradient(colors = categoryGradientColors(category))

@Composable
fun ProductImage(
    product: Product,
    modifier: Modifier = Modifier,
    showOverlayScrim: Boolean = false,
    iconSize: Dp = 44.dp
) {
    Box(
        modifier = modifier.background(categoryGradientBrush(product.category)),
        contentAlignment = Alignment.Center
    ) {
        if (product.imageRes != 0) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            if (showOverlayScrim) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.28f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.14f)
                                )
                            )
                        )
                )
            }
        } else {
            Icon(
                imageVector = productIconFor(product.iconName),
                contentDescription = product.name,
                tint = Color.White,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}
