package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.example.project.theme.AppColors
import org.example.project.theme.AppShapes

@Composable
fun AppDialog(
    title: String,
    body: String,
    emoji: String = "🛡",
    confirmText: String = "Aceptar",
    dismissText: String? = "Cancelar",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppShapes.large)
                .background(AppColors.Surface, AppShapes.large)
                .padding(20.dp),
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(AppShapes.medium)
                            .background(AppColors.AccentDim)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Text(text = emoji, fontSize = 18.sp)
                    }
                    Spacer(Modifier.padding(start = 10.dp))
                    Text(
                        text = title,
                        color = AppColors.OnSurface,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = body,
                    color = AppColors.OnSurfaceMuted,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    if (dismissText != null) {
                        Text(
                            text = dismissText,
                            color = AppColors.OnSurfaceMuted,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clip(AppShapes.pill)
                                .clickable(onClick = onDismiss)
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                        )
                    }
                    Text(
                        text = confirmText,
                        color = AppColors.BackgroundDeep,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(AppShapes.pill)
                            .background(AppColors.Accent)
                            .clickable(onClick = onConfirm)
                            .padding(horizontal = 18.dp, vertical = 10.dp),
                    )
                }
            }
        }
    }
}
