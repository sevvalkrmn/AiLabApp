package com.ktun.ailabapp.presentation.ui.components.inputs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.ktun.ailabapp.ui.theme.AiLabTheme
import com.ktun.ailabapp.ui.theme.AppDimensions
import com.ktun.ailabapp.ui.theme.AppSpacing

@Composable
fun AiLabTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    prefix: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    maxLines: Int = 1,
    minLines: Int = 1,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
            placeholder = if (placeholder.isNotEmpty()) {
                { Text(placeholder, style = MaterialTheme.typography.bodyMedium) }
            } else null,
            prefix = if (prefix != null) {
                { Text(prefix, style = MaterialTheme.typography.bodyLarge) }
            } else null,
            leadingIcon = leadingIcon,
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(AppDimensions.iconSizeMd),
                        )
                    }
                }
            } else trailingIcon,
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            isError = isError,
            enabled = enabled,
            maxLines = maxLines,
            minLines = minLines,
            singleLine = maxLines == 1,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .height(AppDimensions.buttonHeightLarge),
            colors = if (AiLabTheme.isDark) {
                // Açık temada MD3 varsayılanları (yukarıdaki else dalı) hiç
                // değişmeden korunuyor. Koyu temada odak/imleç rengi primary
                // DEĞİL, daha açık "focusAccent" (#7180FF); placeholder ve
                // devre dışı içerik ise "textDisabled" (#858CA8) — bkz. spec.
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AiLabTheme.extendedColors.focusAccent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    disabledBorderColor = AiLabTheme.extendedColors.textDisabled,
                    cursorColor = AiLabTheme.extendedColors.focusAccent,
                    errorCursorColor = MaterialTheme.colorScheme.error,
                    focusedLabelColor = AiLabTheme.extendedColors.focusAccent,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = AiLabTheme.extendedColors.textDisabled,
                    errorLabelColor = MaterialTheme.colorScheme.error,
                    focusedPlaceholderColor = AiLabTheme.extendedColors.textDisabled,
                    unfocusedPlaceholderColor = AiLabTheme.extendedColors.textDisabled,
                    disabledPlaceholderColor = AiLabTheme.extendedColors.textDisabled,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledTextColor = AiLabTheme.extendedColors.textDisabled,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = AiLabTheme.extendedColors.textDisabled,
                    errorTrailingIconColor = MaterialTheme.colorScheme.error,
                )
            } else {
                OutlinedTextFieldDefaults.colors()
            },
            textStyle = MaterialTheme.typography.bodyLarge,
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(
                    start = AppSpacing.md,
                    top = AppSpacing.xxs,
                ),
            )
        }
    }
}
