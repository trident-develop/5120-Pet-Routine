package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.PlatformWebView
import org.example.project.data.LocalAppStrings
import org.example.project.theme.AppColors
import org.example.project.theme.AppShapes
import org.example.project.ui.components.PrimaryButton

@Composable
fun WebViewScreen(
    url: String,
    title: String,
    onBack: () -> Unit,
) {
    val insets = WindowInsets.safeDrawing.asPaddingValues()
    val s = LocalAppStrings.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BackgroundDeep),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = insets.calculateTopPadding())
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(AppShapes.medium)
                    .background(AppColors.Surface, AppShapes.medium)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "←", color = AppColors.OnSurface, fontSize = 22.sp)
            }
            Text(
                text = title,
                color = AppColors.OnSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Box(modifier = Modifier.size(40.dp))
        }
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            PlatformWebView(url = url, modifier = Modifier.fillMaxSize())
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = insets.calculateBottomPadding()),
        ) {
            PrimaryButton(
                text = s.close,
                modifier = Modifier.fillMaxWidth(),
                onClick = onBack,
            )
        }
    }
}
