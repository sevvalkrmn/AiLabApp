package com.ktun.ailabapp.presentation.ui.components.buttons

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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ktun.ailabapp.ui.theme.AppDimensions
import com.ktun.ailabapp.ui.theme.AppSpacing
import com.ktun.ailabapp.ui.theme.BorderGray
import com.ktun.ailabapp.ui.theme.ErrorRed
import com.ktun.ailabapp.ui.theme.PrimaryBlue
import com.ktun.ailabapp.ui.theme.White

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

    when (variant) {
        AiLabButtonVariant.Secondary -> OutlinedButton(
            onClick = onClick,
            modifier = widthModifier,
            enabled = enabled && !isLoading,
            shape = shape,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = AppDimensions.borderWidth),
        ) { ButtonContent(text, isLoading, leadingIcon) }

        AiLabButtonVariant.Ghost -> TextButton(
            onClick = onClick,
            modifier = widthModifier,
            enabled = enabled && !isLoading,
            shape = shape,
            colors = ButtonDefaults.textButtonColors(contentColor = PrimaryBlue),
        ) { ButtonContent(text, isLoading, leadingIcon) }

        AiLabButtonVariant.Danger -> Button(
            onClick = onClick,
            modifier = widthModifier,
            enabled = enabled && !isLoading,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = ErrorRed,
                contentColor = White,
                disabledContainerColor = ErrorRed.copy(alpha = 0.4f),
                disabledContentColor = White.copy(alpha = 0.6f),
            ),
        ) { ButtonContent(text, isLoading, leadingIcon) }

        AiLabButtonVariant.Primary -> Button(
            onClick = onClick,
            modifier = widthModifier,
            enabled = enabled && !isLoading,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue,
                contentColor = White,
                disabledContainerColor = PrimaryBlue.copy(alpha = 0.4f),
                disabledContentColor = White.copy(alpha = 0.6f),
            ),
        ) { ButtonContent(text, isLoading, leadingIcon) }
    }
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
            color = White,
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
