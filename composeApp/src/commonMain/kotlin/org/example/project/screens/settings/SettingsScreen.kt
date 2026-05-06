package org.example.project.screens.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.AppLanguage
import org.example.project.data.LocalAppStrings
import org.example.project.data.LocalPetState
import org.example.project.platform.platformLegalLinks
import org.example.project.theme.AppColors
import org.example.project.theme.AppShapes
import org.example.project.ui.components.AppCard
import org.example.project.ui.components.ButtonStyle
import org.example.project.ui.components.PrimaryButton
import org.example.project.ui.components.ScreenHeader

@Composable
fun SettingsScreen(openWebView: (url: String, title: String) -> Unit) {
    val state = LocalPetState.current
    val s = LocalAppStrings.current

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            eyebrow = s.settingsEyebrow,
            title = s.settingsTitle,
            subtitle = s.settingsSubtitle,
        )
        Spacer(Modifier.height(14.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                SectionCard(title = s.sectionAppearance, emoji = "🎨") {
                    ToggleRow(
                        title = s.darkMode,
                        subtitle = s.darkModeSubtitle,
                        checked = state.darkMode,
                        onChange = { state.changeDarkMode(it) },
                    )
                    Divider()
                    LanguageRow(
                        selected = state.language,
                        onSelect = { state.changeLanguage(it) },
                    )
                }
            }
            item {
                SectionCard(title = s.sectionData, emoji = "🗄") {
                    Text(
                        text = s.yourPets,
                        color = AppColors.OnSurfaceMuted,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(8.dp))
                    if (state.pets.isEmpty()) {
                        Text(
                            text = s.noPetsYet,
                            color = AppColors.OnSurfaceMuted,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    } else {
                        state.pets.forEachIndexed { idx, pet ->
                            PetDeleteRow(
                                emoji = pet.type.emoji,
                                name = pet.name,
                                onDelete = { state.removePet(pet.id) },
                            )
                            if (idx != state.pets.lastIndex) Divider()
                        }
                    }
                }
            }
            item {
                SectionCard(title = s.sectionLegal, emoji = "📜") {
                    val links = platformLegalLinks()
                    links.forEachIndexed { idx, link ->
                        PrimaryButton(
                            text = link.title,
                            modifier = Modifier.fillMaxWidth(),
                            style = if (idx == 0) ButtonStyle.Secondary else ButtonStyle.Ghost,
                            leadingEmoji = if (idx == 0) "🛡" else "📄",
                            onClick = { openWebView(link.url, link.title) },
                        )
                        if (idx != links.lastIndex) Spacer(Modifier.height(8.dp))
                    }
                }
            }
            item {
                Text(
                    text = s.versionFooter,
                    color = AppColors.OnSurfaceDim,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(vertical = 6.dp),
                )
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun PetDeleteRow(emoji: String, name: String, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = emoji, fontSize = 28.sp)
        Spacer(Modifier.size(14.dp))
        Text(
            text = name,
            color = AppColors.OnSurface,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "✕",
            color = AppColors.Danger,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clip(AppShapes.pill)
                .clickable(onClick = onDelete)
                .padding(horizontal = 16.dp, vertical = 10.dp),
        )
    }
}

@Composable
private fun SectionCard(title: String, emoji: String, content: @Composable () -> Unit) {
    AppCard(contentPadding = 22.dp) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = emoji, fontSize = 22.sp)
                Spacer(Modifier.size(12.dp))
                Text(
                    text = title,
                    color = AppColors.OnSurface,
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
            Spacer(Modifier.height(18.dp))
            content()
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChange(!checked) }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = AppColors.OnSurface, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(2.dp))
            Text(text = subtitle, color = AppColors.OnSurfaceMuted, style = MaterialTheme.typography.bodyMedium)
        }
        AnimatedToggle(checked = checked)
    }
}

@Composable
private fun AnimatedToggle(checked: Boolean) {
    val bg by animateColorAsState(
        if (checked) AppColors.Accent else AppColors.SurfaceElevated,
        label = "tg-bg",
    )
    val knobX by animateDpAsState(
        if (checked) 22.dp else 2.dp,
        animationSpec = spring(stiffness = 360f, dampingRatio = 0.55f),
        label = "tg-x",
    )
    val knobScale by animateFloatAsState(
        if (checked) 1f else 0.95f,
        label = "tg-scale",
    )
    Box(
        modifier = Modifier
            .width(48.dp)
            .height(28.dp)
            .clip(AppShapes.pill)
            .background(bg, AppShapes.pill)
            .border(1.dp, AppColors.CardBorder, AppShapes.pill),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .padding(start = knobX.coerceAtLeast(0.dp))
                .size(24.dp)
                .scale(knobScale)
                .clip(CircleShape)
                .background(AppColors.OnSurface),
        )
    }
}

@Composable
private fun LanguageRow(selected: AppLanguage, onSelect: (AppLanguage) -> Unit) {
    val s = LocalAppStrings.current
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = s.language,
                color = AppColors.OnSurface,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Text(text = selected.displayName, color = AppColors.OnSurfaceMuted, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(8.dp))
        AppLanguage.entries.forEach { lang ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(lang) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (lang == selected) "●  " else "○  ",
                    color = if (lang == selected) AppColors.Accent else AppColors.OnSurfaceDim,
                    fontSize = 18.sp,
                )
                Text(
                    text = lang.displayName,
                    color = AppColors.OnSurface,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(AppColors.Divider)
            .padding(vertical = 6.dp),
    )
}
