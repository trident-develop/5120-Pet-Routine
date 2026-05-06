package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.example.project.theme.AppColors
import org.example.project.theme.AppShapes

@Composable
fun AppTextField(
    value: TextFieldValue,
    onChange: (TextFieldValue) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    supporting: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    singleLine: Boolean = true,
    onImeAction: (() -> Unit)? = null,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        if (label != null || supporting != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (label != null) {
                    Text(
                        text = label,
                        color = AppColors.OnSurfaceMuted,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                if (supporting != null) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = supporting,
                        color = AppColors.OnSurfaceDim,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppShapes.medium)
                .background(AppColors.SurfaceElevated, AppShapes.medium)
                .border(1.dp, AppColors.CardBorder, AppShapes.medium)
                .padding(horizontal = 12.dp, vertical = 12.dp),
        ) {
            if (value.text.isEmpty()) {
                Text(text = hint, color = AppColors.OnSurfaceDim)
            }
            BasicTextField(
                value = value,
                onValueChange = onChange,
                singleLine = singleLine,
                cursorBrush = SolidColor(AppColors.Accent),
                textStyle = LocalTextStyle.current.copy(color = AppColors.OnSurface),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
                keyboardActions = KeyboardActions(
                    onDone = { onImeAction?.invoke() ?: focusManager.clearFocus() },
                    onSearch = { onImeAction?.invoke() ?: focusManager.clearFocus() },
                    onSend = { onImeAction?.invoke() ?: focusManager.clearFocus() },
                    onGo = { onImeAction?.invoke() ?: focusManager.clearFocus() },
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { state ->
                        if (!state.isFocused) keyboardController?.hide()
                    },
            )
        }
    }
}
