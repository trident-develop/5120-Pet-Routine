package org.example.project.screens.explore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.Animal
import org.example.project.data.AnimalCategory
import org.example.project.data.AnimalRepository
import org.example.project.data.LocalAppLanguage
import org.example.project.data.LocalAppStrings
import org.example.project.platform.BackHandler
import org.example.project.theme.AppColors
import org.example.project.ui.components.AnimalEmojiArt
import org.example.project.ui.components.AppCard
import org.example.project.ui.components.AppFilterChip
import org.example.project.ui.components.DangerBadge
import org.example.project.ui.components.ScreenHeader

@Composable
fun ExploreScreen() {
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var categoryFilter by remember { mutableStateOf<AnimalCategory?>(null) }
    var selected by remember { mutableStateOf<Animal?>(null) }
    val lang = LocalAppLanguage.current
    val animals = remember(lang) { AnimalRepository.all(lang) }

    val filtered = remember(query.text, categoryFilter, animals) {
        animals
            .filter { animal -> categoryFilter == null || animal.category == categoryFilter }
            .filter {
                val q = query.text.trim()
                q.isEmpty() || it.name.contains(q, ignoreCase = true)
            }
    }

    val focusManager = LocalFocusManager.current
    val s = LocalAppStrings.current

    BackHandler(enabled = selected != null) { selected = null }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { focusManager.clearFocus() },
            ),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(
                eyebrow = s.exploreEyebrow,
                title = s.exploreTitle,
                subtitle = s.exploreSubtitle(animals.size),
            )
            Spacer(Modifier.height(14.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item { SearchField(query = query, onQueryChange = { query = it }) }
                item {
                    TypeFilter(
                        selected = categoryFilter,
                        onSelect = {
                            focusManager.clearFocus()
                            categoryFilter = it
                        },
                    )
                }
                item { FactOfTheDayCard() }
                item {
                    FeaturedAnimal(
                        animal = filtered.firstOrNull() ?: animals.first(),
                    ) {
                        focusManager.clearFocus()
                        selected = it
                    }
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = s.allAnimals,
                            color = AppColors.OnSurface,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "${filtered.size}",
                            color = AppColors.OnSurfaceMuted,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
                if (filtered.isEmpty()) {
                    item { EmptyResults() }
                }
                items(filtered, key = { it.id }) { animal ->
                    AnimalRow(animal = animal, onClick = {
                        focusManager.clearFocus()
                        selected = animal
                    })
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }

        AnimatedVisibility(
            visible = selected != null,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            selected?.let { animal ->
                AnimalDetailSheet(animal = animal, onClose = { selected = null })
            }
        }
    }
}

@Composable
private fun TypeFilter(
    selected: AnimalCategory?,
    onSelect: (AnimalCategory?) -> Unit,
) {
    val s = LocalAppStrings.current
    val items: List<Pair<String, AnimalCategory?>> = listOf(s.all to null) +
        AnimalCategory.entries.map { s.animalCategory(it) to it }
    Column {
        Text(
            text = s.byType,
            color = AppColors.OnSurfaceMuted,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(items, key = { it.first }) { (label, key) ->
                AppFilterChip(
                    text = label,
                    selected = selected == key,
                    onClick = { onSelect(key) },
                    large = true,
                )
            }
        }
    }
}

@Composable
private fun SearchField(query: TextFieldValue, onQueryChange: (TextFieldValue) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val s = LocalAppStrings.current
    AppCard(contentPadding = 14.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "🔎", fontSize = 16.sp)
            Spacer(Modifier.size(10.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.text.isEmpty()) {
                    Text(
                        text = s.searchAnimals,
                        color = AppColors.OnSurfaceDim,
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    cursorBrush = SolidColor(AppColors.Accent),
                    textStyle = LocalTextStyle.current.copy(color = AppColors.OnSurface),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { state ->
                            if (!state.isFocused) keyboardController?.hide()
                        },
                )
            }
        }
    }
}

@Composable
private fun FactOfTheDayCard() {
    val s = LocalAppStrings.current
    val lang = LocalAppLanguage.current
    val fact = remember(lang) { AnimalRepository.factsOfTheDay(lang).random() }
    AppCard(
        backgroundBrush = Brush.linearGradient(listOf(AppColors.Accent, AppColors.AccentSoft)),
        border = null,
        contentPadding = 18.dp,
    ) {
        Column {
            Text(
                text = s.factOfTheDay,
                color = AppColors.BackgroundDeep.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = fact,
                color = AppColors.BackgroundDeep,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun FeaturedAnimal(animal: Animal, onClick: (Animal) -> Unit) {
    val s = LocalAppStrings.current
    AppCard(
        onClick = { onClick(animal) },
        backgroundBrush = AppColors.surfaceGradient,
        contentPadding = 18.dp,
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = s.featured,
                    color = AppColors.Accent,
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(Modifier.weight(1f))
                DangerBadge(animal.danger)
            }
            Spacer(Modifier.height(12.dp))
            AnimalEmojiArt(
                emoji = animal.emoji,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                fontSize = 88,
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = animal.name,
                color = AppColors.OnSurface,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = animal.regions.joinToString(" · ") { s.region(it) },
                color = AppColors.OnSurfaceMuted,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = animal.shortFact,
                color = AppColors.OnSurfaceMuted,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun AnimalRow(animal: Animal, onClick: () -> Unit) {
    AppCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimalEmojiArt(
                emoji = animal.emoji,
                modifier = Modifier.size(64.dp),
                fontSize = 36,
            )
            Spacer(Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = animal.name,
                        color = AppColors.OnSurface,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(Modifier.weight(1f))
                    DangerBadge(animal.danger)
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = animal.countries.joinToString(", "),
                    color = AppColors.Accent,
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = animal.shortFact,
                    color = AppColors.OnSurfaceMuted,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                )
            }
        }
    }
}

@Composable
private fun EmptyResults() {
    val s = LocalAppStrings.current
    AppCard {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "🦴", fontSize = 36.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = s.noTracks,
                color = AppColors.OnSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = s.tryDifferentFilter,
                color = AppColors.OnSurfaceMuted,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
