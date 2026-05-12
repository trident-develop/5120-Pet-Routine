package org.example.project.screens.explore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.Animal
import org.example.project.data.AnimalRepository
import org.example.project.data.LocalAppLanguage
import org.example.project.data.LocalAppStrings
import org.example.project.platform.BackHandler
import org.example.project.theme.AppColors
import org.example.project.theme.AppShapes
import org.example.project.ui.components.AnimalEmojiArt
import org.example.project.ui.components.AppCard
import org.example.project.ui.components.DangerBadge
import org.example.project.ui.components.PrimaryButton

@Composable
fun AnimalDetailSheet(animal: Animal, onClose: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(animal.id) { visible = true }

    val scrim by animateFloatAsState(
        targetValue = if (visible) 0.8f else 0f,
        animationSpec = tween(220),
        label = "scrim",
    )

    var pickerOpen by remember(animal.id) { mutableStateOf(false) }
    var compareWith by remember(animal.id) { mutableStateOf<Animal?>(null) }

    // Order matters: handlers added later are invoked first.
    // compareWith handler is registered before pickerOpen so the picker wins
    // when both are technically active during transitions.
    BackHandler(enabled = compareWith != null && !pickerOpen) { compareWith = null }
    BackHandler(enabled = pickerOpen) { pickerOpen = false }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = scrim))
            .clickable(onClick = onClose),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(AppColors.Background)
                .clickable(enabled = false, onClick = {}),
        ) {
            val opponent = compareWith
            if (opponent != null) {
                CompareView(
                    left = animal,
                    right = opponent,
                    onBack = { compareWith = null },
                    onClose = onClose,
                )
            } else {
                DetailContent(
                    animal = animal,
                    onCompare = { pickerOpen = true },
                    onClose = onClose,
                )
            }

            AnimatedVisibility(
                visible = pickerOpen,
                enter = fadeIn(tween(180)),
                exit = fadeOut(tween(140)),
            ) {
                ComparePicker(
                    excludeId = animal.id,
                    onPick = {
                        compareWith = it
                        pickerOpen = false
                    },
                    onDismiss = { pickerOpen = false },
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    animal: Animal,
    onCompare: () -> Unit,
    onClose: () -> Unit,
) {
    val s = LocalAppStrings.current
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.Background)
                .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
            Box(
                Modifier
                    .height(4.dp)
                    .fillMaxWidth(0.18f)
                    .clip(AppShapes.pill)
                    .background(AppColors.SurfaceElevated)
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "✕  ${LocalAppStrings.current.close}",
                color = AppColors.OnSurfaceMuted,
                modifier = Modifier
                    .clip(AppShapes.pill)
                    .clickable(onClick = onClose)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 20.dp),
        ) {
            item {
                AnimalEmojiArt(
                    emoji = animal.emoji,
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    fontSize = 110,
                )
            }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = animal.name,
                        color = AppColors.OnSurface,
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Text(
                        text = animal.regions.joinToString(" · ") { s.region(it) },
                        color = AppColors.Accent,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                DangerBadge(animal.danger)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickStat(s.statSpeed, "${animal.speedKmh}", "km/h", Modifier.weight(1f))
                QuickStat(s.statWeight, "${animal.weightKg}", "kg", Modifier.weight(1f))
                QuickStat(s.statLifespan, "${animal.lifespanYears}", s.statLifespanUnit, Modifier.weight(1f))
            }
        }
        item { InfoBlock(title = s.behavior, body = animal.behavior, emoji = "🧠") }
        item { InfoBlock(title = s.habitat, body = animal.habitat, emoji = "🌲") }
        item { InfoBlock(title = s.diet, body = animal.diet, emoji = "🍖") }
        item { InfoBlock(title = s.dangerLevel, body = s.dangerLabel(animal.danger), emoji = "⚠️") }
        item { CountriesBlock(countries = animal.countries) }
        item { FactsBlock(facts = animal.facts) }
        item {
            PrimaryButton(
                text = s.compareWithAnother,
                modifier = Modifier.fillMaxWidth(),
                leadingEmoji = "⚖️",
                onClick = onCompare,
            )
        }
            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

@Composable
private fun ComparePicker(
    excludeId: String,
    onPick: (Animal) -> Unit,
    onDismiss: () -> Unit,
) {
    val lang = LocalAppLanguage.current
    val candidates = remember(excludeId, lang) {
        AnimalRepository.all(lang).filter { it.id != excludeId }
    }
    val s = LocalAppStrings.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(onClick = onDismiss),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(AppColors.Background)
                .clickable(enabled = false, onClick = {}),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.Background)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = s.compareWith,
                        color = AppColors.OnSurface,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                        text = s.compareWithSubtitle(candidates.size),
                        color = AppColors.OnSurfaceMuted,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Text(
                    text = "✕",
                    color = AppColors.OnSurfaceMuted,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .clip(AppShapes.pill)
                        .clickable(onClick = onDismiss)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 20.dp),
            ) {
                items(candidates, key = { it.id }) { candidate ->
                    PickerRow(animal = candidate, onClick = { onPick(candidate) })
                }
                item { Spacer(Modifier.height(40.dp)) }
            }
        }
    }
}

@Composable
private fun PickerRow(animal: Animal, onClick: () -> Unit) {
    val s = LocalAppStrings.current
    AppCard(onClick = onClick, contentPadding = 12.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimalEmojiArt(
                emoji = animal.emoji,
                modifier = Modifier.size(48.dp),
                fontSize = 26,
            )
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = animal.name,
                    color = AppColors.OnSurface,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "${s.animalCategory(animal.category)} · ${animal.speedKmh} km/h · ${animal.weightKg} kg",
                    color = AppColors.OnSurfaceMuted,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Text(text = "→", color = AppColors.Accent, fontSize = 18.sp)
        }
    }
}

@Composable
private fun CompareView(
    left: Animal,
    right: Animal,
    onBack: () -> Unit,
    onClose: () -> Unit,
) {
    val s = LocalAppStrings.current
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.Background)
                .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
            Text(
                text = "←  ${s.back}",
                color = AppColors.OnSurfaceMuted,
                modifier = Modifier
                    .clip(AppShapes.pill)
                    .clickable(onClick = onBack)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "✕  ${LocalAppStrings.current.close}",
                color = AppColors.OnSurfaceMuted,
                modifier = Modifier
                    .clip(AppShapes.pill)
                    .clickable(onClick = onClose)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 20.dp),
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CompareHeaderCard(animal = left, modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier.size(width = 32.dp, height = 70.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "VS", color = AppColors.Accent, fontWeight = FontWeight.Bold)
                }
                CompareHeaderCard(animal = right, modifier = Modifier.weight(1f))
            }
        }
        item {
            CompareSection(title = s.stats) {
                NumericCompareRow(s.statSpeed, left.speedKmh, right.speedKmh, "km/h", higherWins = true)
                NumericCompareRow(s.statWeight, left.weightKg, right.weightKg, "kg", higherWins = true)
                NumericCompareRow(s.statLifespan, left.lifespanYears, right.lifespanYears, s.statLifespanUnit, higherWins = true)
                NumericCompareRow(s.danger, left.danger.level, right.danger.level, "/4", higherWins = true)
            }
        }
        item {
            CompareSection(title = s.profile) {
                TextCompareRow(s.type, s.animalCategory(left.category), s.animalCategory(right.category))
                TextCompareRow(s.danger, s.dangerLabel(left.danger), s.dangerLabel(right.danger))
                TextCompareRow(
                    s.regions,
                    left.regions.joinToString(", ") { s.region(it) },
                    right.regions.joinToString(", ") { s.region(it) },
                )
                TextCompareRow(s.diet, left.diet, right.diet)
                TextCompareRow(s.habitat, left.habitat, right.habitat)
                TextCompareRow(
                    s.countries,
                    left.countries.joinToString(", "),
                    right.countries.joinToString(", "),
                )
            }
        }
            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

@Composable
private fun CompareHeaderCard(animal: Animal, modifier: Modifier = Modifier) {
    val s = LocalAppStrings.current
    AppCard(modifier = modifier, contentPadding = 12.dp) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            AnimalEmojiArt(
                emoji = animal.emoji,
                modifier = Modifier.fillMaxWidth().height(80.dp),
                fontSize = 48,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = animal.name,
                color = AppColors.OnSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = s.animalCategory(animal.category),
                color = AppColors.OnSurfaceMuted,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun CompareSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    AppCard {
        Column {
            Text(
                text = title,
                color = AppColors.OnSurface,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun NumericCompareRow(
    label: String,
    leftValue: Int,
    rightValue: Int,
    unit: String,
    higherWins: Boolean,
) {
    val leftWins = if (higherWins) leftValue > rightValue else leftValue < rightValue
    val rightWins = if (higherWins) rightValue > leftValue else rightValue < leftValue
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
        CompareValueCell(
            value = "$leftValue $unit",
            highlighted = leftWins,
            modifier = Modifier.weight(1f),
            alignment = Alignment.End,
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .size(width = 58.dp, height = 26.dp)
                .clip(AppShapes.pill)
                .background(AppColors.SurfaceMuted),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                color = AppColors.OnSurfaceMuted,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
            )
        }
        CompareValueCell(
            value = "$rightValue $unit",
            highlighted = rightWins,
            modifier = Modifier.weight(1f),
            alignment = Alignment.Start,
        )
    }
}

@Composable
private fun CompareValueCell(
    value: String,
    highlighted: Boolean,
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = alignment,
    ) {
        Box(
            modifier = Modifier
                .clip(AppShapes.pill)
                .background(if (highlighted) AppColors.AccentDim else Color.Transparent)
                .border(
                    width = 1.dp,
                    color = if (highlighted) AppColors.Accent else AppColors.CardBorder,
                    shape = AppShapes.pill,
                )
                .padding(horizontal = 8.dp, vertical = 5.dp),
        ) {
            Text(
                text = value,
                color = if (highlighted) AppColors.Accent else AppColors.OnSurface,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun TextCompareRow(label: String, leftValue: String, rightValue: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label.uppercase(),
            color = AppColors.OnSurfaceDim,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = leftValue,
                color = AppColors.OnSurface,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f).padding(end = 8.dp),
            )
            Box(
                modifier = Modifier
                    .size(width = 1.dp, height = 24.dp)
                    .background(AppColors.Divider),
            )
            Text(
                text = rightValue,
                color = AppColors.OnSurface,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f).padding(start = 8.dp),
            )
        }
    }
}

@Composable
private fun QuickStat(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    AppCard(modifier = modifier, contentPadding = 12.dp) {
        Column {
            Text(text = label, color = AppColors.OnSurfaceMuted, style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, color = AppColors.OnSurface, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Text(text = " $unit", color = AppColors.OnSurfaceMuted, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun InfoBlock(title: String, body: String, emoji: String) {
    AppCard {
        Row(verticalAlignment = Alignment.Top) {
            Text(text = emoji, fontSize = 22.sp)
            Spacer(Modifier.size(10.dp))
            Column {
                Text(
                    text = title,
                    color = AppColors.OnSurface,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = body,
                    color = AppColors.OnSurfaceMuted,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CountriesBlock(countries: List<String>) {
    val s = LocalAppStrings.current
    AppCard {
        Column {
            Text(
                text = s.countriesTitle,
                color = AppColors.OnSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                countries.forEach { c ->
                    Text(
                        text = c,
                        color = AppColors.OnSurface,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .clip(AppShapes.pill)
                            .background(AppColors.SurfaceElevated)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun FactsBlock(facts: List<String>) {
    val s = LocalAppStrings.current
    AppCard {
        Column {
            Text(
                text = s.factsTitle,
                color = AppColors.OnSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(10.dp))
            facts.forEachIndexed { index, fact ->
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(AppShapes.pill)
                            .background(AppColors.AccentDim),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "${index + 1}", color = AppColors.Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.size(10.dp))
                    Text(
                        text = fact,
                        color = AppColors.OnSurfaceMuted,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (index != facts.lastIndex) Spacer(Modifier.height(8.dp))
            }
        }
    }
}
