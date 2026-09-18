package com.ktun.ailabapp.presentation.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ktun.ailabapp.ui.theme.AiLabTheme
import com.ktun.ailabapp.ui.theme.AppDimensions
import com.ktun.ailabapp.ui.theme.AppSpacing

enum class AiLabButtonVariant { Primary, Secondary, Danger, Ghost }
enum class AiLabButtonSize { Large, Medium, Small }

@Composable
fun AiLabButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AiLabButtonVariant = AiLabButtonVariant.Primary,
    size: AiLabButtonSize = AiLabButtonSize.Large,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fillWidth: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val height = when (size) {
        AiLabButtonSize.Large  -> AppDimensions.buttonHeightLarge
        AiLabButtonSize.Medium -> AppDimensions.buttonHeightMedium
        AiLabButtonSize.Small  -> AppDimensions.buttonHeightSmall
    }
    val widthModifier = if (fillWidth) modifier.fillMaxWidth().height(height)
                        else modifier.height(height)
    val shape = MaterialTheme.shapes.medium
    val isEnabled = enabled && !isLoading
    val isDark = AiLabTheme.isDark

    when (variant) {
        AiLabButtonVariant.Secondary -> {
            // Açık temada davranış birebir korunur (gri outline + primary metin).
            // Koyu temada kenarlık/metin "focusAccent" (#7180FF), basılıyken
            // zemin "surfaceVariant" (#171E45) ile dolar.
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val contentColor = if (isDark) AiLabTheme.extendedColors.focusAccent else MaterialTheme.colorScheme.primary
            val borderColor = when {
                !isEnabled -> MaterialTheme.colorScheme.outline
                isDark -> AiLabTheme.extendedColors.focusAccent
                else -> MaterialTheme.colorScheme.outline
            }
            OutlinedButton(
                onClick = onClick,
                modifier = widthModifier,
                enabled = isEnabled,
                shape = shape,
                interactionSource = interactionSource,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isDark && isPressed) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                    contentColor = contentColor,
                    disabledContentColor = if (isDark) MaterialTheme.colorScheme.outline else contentColor.copy(alpha = 0.38f),
                ),
                border = BorderStroke(AppDimensions.borderWidth, borderColor),
            ) { ButtonContent(text, isLoading, leadingIcon) }
        }

        AiLabButtonVariant.Ghost -> TextButton(
            onClick = onClick,
            modifier = widthModifier,
            enabled = isEnabled,
            shape = shape,
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
        ) { ButtonContent(text, isLoading, leadingIcon) }

        AiLabButtonVariant.Danger -> Button(
            onClick = onClick,
            modifier = widthModifier,
            enabled = isEnabled,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            ),
        ) { ButtonContent(text, isLoading, leadingIcon) }

        AiLabButtonVariant.Primary -> {
            // Açık temada davranış birebir korunur. Koyu temada basılıyken
            // zemin "primaryPressed" (#19269F) olur; devre dışı durumda
            // zemin/metin sabit "outline"/"onSurfaceVariant" tonlarını kullanır.
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val containerColor = if (isDark && isPressed) AiLabTheme.extendedColors.primaryPressed else MaterialTheme.colorScheme.primary
            Button(
                onClick = onClick,
                modifier = widthModifier,
                enabled = isEnabled,
                shape = shape,
                interactionSource = interactionSource,
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = if (isDark) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    disabledContentColor = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                ),
            ) { ButtonContent(text, isLoading, leadingIcon) }
        }
    }
}

/**
 * MD3'te karşılığı olmayan gradyan/gölge efektli özel bir buton stiliydi.
 * Artık standart dolu (filled) MD3 butonuna eşleniyor; tutarlılık için
 * doğrudan [AiLabButton] kullanın.
 */
@Deprecated(
    message = "MD3 geçişi: standart dolu buton için AiLabButton kullanın",
    replaceWith = ReplaceWith(
        "AiLabButton(text = text, onClick = onClick, modifier = modifier, isLoading = isLoading, enabled = enabled)"
    ),
)
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
) {
    AiLabButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        variant = AiLabButtonVariant.Primary,
        isLoading = isLoading,
        enabled = enabled,
    )
}

@Composable
private fun RowScope.ButtonContent(
    text: String,
    isLoading: Boolean,
    leadingIcon: (@Composable () -> Unit)?,
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(AppDimensions.progressSizeMd),
            color = LocalContentColor.current,
            strokeWidth = 2.dp,
        )
    } else {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(AppSpacing.sm))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
