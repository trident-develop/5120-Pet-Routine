package org.example.project.screens.pet

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.example.project.data.LocalAppStrings
import org.example.project.data.LocalPetState
import org.example.project.data.MealSlot
import org.example.project.data.Pet
import org.example.project.data.PetNote
import org.example.project.data.Vaccine
import org.example.project.data.WeekDay
import org.example.project.data.formatPetWeight
import org.example.project.data.mealKey
import org.example.project.platform.PlatformImage
import org.example.project.platform.nowDate
import org.example.project.theme.AppColors
import org.example.project.theme.AppShapes
import org.example.project.ui.components.AppCard
import org.example.project.ui.components.AppTextField
import org.example.project.ui.components.LocalSnackbarHost
import org.example.project.ui.components.PrimaryButton

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PetProfilePager(onAddNew: () -> Unit) {
    val state = LocalPetState.current
    if (state.pets.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = state.currentIndex.coerceIn(0, (state.pets.size - 1).coerceAtLeast(0)),
        pageCount = { state.pets.size },
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page -> state.selectPet(page) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            pageSpacing = 8.dp,
        ) { page ->
            val pet = state.pets[page]
            LazyColumn(
                modifier = Modifier.fillMaxSize().imePadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 96.dp),
            ) {
                item { PetHeaderCard(pet) }
                item { MealPlannerCard(pet.id) }
                item { VaccinesCard(pet.id) }
                item { NotesCard(pet.id) }
            }
        }

        if (state.pets.size > 1) {
            PageIndicator(
                count = state.pets.size,
                current = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp),
            )
        }

        AddFab(
            onClick = onAddNew,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 4.dp, bottom = 12.dp),
        )
    }
}

@Composable
private fun PageIndicator(count: Int, current: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(AppShapes.pill)
            .background(AppColors.SurfaceMuted.copy(alpha = 0.85f), AppShapes.pill)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(count) { idx ->
            val active = idx == current
            val width by animateDpAsState(if (active) 18.dp else 6.dp, label = "ind-w")
            val color by animateColorAsState(
                if (active) AppColors.Accent else AppColors.OnSurfaceDim,
                label = "ind-c",
            )
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color, CircleShape),
            )
        }
    }
}

@Composable
private fun AddFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(AppColors.Accent, CircleShape)
            .border(2.dp, AppColors.AccentSoft, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "+",
            color = AppColors.BackgroundDeep,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
        )
    }
}

// ---------- Per-pet sections ----------

@Composable
private fun PetHeaderCard(pet: Pet) {
    val s = LocalAppStrings.current
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundBrush = AppColors.surfaceGradient,
        contentPadding = 18.dp,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PetAvatar(
                photoPath = pet.photoPath,
                fallbackEmoji = pet.type.emoji,
            )
            Spacer(Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pet.name,
                    color = AppColors.OnSurface,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = listOfNotNull(
                        s.petType(pet.type),
                        pet.breed.takeIf { it.isNotBlank() },
                    ).joinToString(" · "),
                    color = AppColors.Accent,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(8.dp))
                Row {
                    PetMetaPill(label = s.ageLabel, value = s.ageValue(pet.ageYears))
                    Spacer(Modifier.size(8.dp))
                    PetMetaPill(label = s.weightLabel, value = formatPetWeight(pet.weightGrams))
                }
            }
        }
    }
}

@Composable
private fun PetMetaPill(label: String, value: String) {
    Column(
        modifier = Modifier
            .clip(AppShapes.medium)
            .background(AppColors.SurfaceMuted, AppShapes.medium)
            .border(1.dp, AppColors.CardBorder, AppShapes.medium)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            color = AppColors.OnSurfaceDim,
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            text = value,
            color = AppColors.OnSurface,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun PetAvatar(
    photoPath: String?,
    fallbackEmoji: String,
) {
    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(AppColors.AccentDim, CircleShape)
            .border(2.dp, AppColors.Accent, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (photoPath != null) {
            PlatformImage(path = photoPath, modifier = Modifier.size(96.dp).clip(CircleShape))
        } else {
            Text(text = fallbackEmoji, fontSize = 38.sp)
        }
    }
}

// ---- Meal planner ----

@Composable
private fun MealPlannerCard(petId: Long) {
    var selectedDay by remember(petId) { mutableStateOf(WeekDay.entries.first()) }
    val s = LocalAppStrings.current

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            SectionHeader(emoji = "🍽", title = s.mealPlanner, subtitle = s.mealPlannerSubtitle)
            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(WeekDay.entries.toList(), selectedDay) { selectedDay = it }
            }
            Spacer(Modifier.height(14.dp))
            MealSlot.entries.forEach { slot ->
                MealRow(petId = petId, day = selectedDay, slot = slot)
                if (slot != MealSlot.entries.last()) Spacer(Modifier.height(10.dp))
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.items(
    days: List<WeekDay>,
    selected: WeekDay,
    onSelect: (WeekDay) -> Unit,
) {
    items(days.size, key = { days[it].name }) { index ->
        val day = days[index]
        DayChip(day = day, selected = day == selected, onClick = { onSelect(day) })
    }
}

@Composable
private fun DayChip(day: WeekDay, selected: Boolean, onClick: () -> Unit) {
    val s = LocalAppStrings.current
    val bg by animateColorAsState(
        if (selected) AppColors.Accent else AppColors.SurfaceElevated,
        label = "day-bg",
    )
    val fg by animateColorAsState(
        if (selected) AppColors.BackgroundDeep else AppColors.OnSurfaceMuted,
        label = "day-fg",
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(AppShapes.medium)
            .background(bg, AppShapes.medium)
            .border(
                1.dp,
                if (selected) AppColors.Accent else AppColors.CardBorder,
                AppShapes.medium,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(text = s.weekDayShort(day), color = fg, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun MealRow(petId: Long, day: WeekDay, slot: MealSlot) {
    val state = LocalPetState.current
    val s = LocalAppStrings.current
    val key = mealKey(day, slot)
    val raw = state.mealsFor(petId)[key].orEmpty()
    var field by remember(petId, key) { mutableStateOf(TextFieldValue(raw)) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(AppShapes.medium)
                .background(AppColors.SurfaceElevated, AppShapes.medium),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = slot.emoji, fontSize = 18.sp)
        }
        Spacer(Modifier.size(10.dp))
        AppTextField(
            value = field,
            onChange = { tfv ->
                field = tfv
                state.setMeal(petId, day, slot, tfv.text)
            },
            hint = s.mealSlot(slot),
            modifier = Modifier.weight(1f),
        )
    }
}

// ---- Vaccines ----

@Composable
private fun VaccinesCard(petId: Long) {
    val state = LocalPetState.current
    val snackbar = LocalSnackbarHost.current
    val scope = rememberCoroutineScope()
    val s = LocalAppStrings.current
    var name by remember(petId) { mutableStateOf(TextFieldValue("")) }
    var date by remember(petId) { mutableStateOf(TextFieldValue(nowDate())) }
    val vaccines = state.vaccinesFor(petId)

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            SectionHeader(emoji = "💉", title = s.vaccines, subtitle = s.vaccinesSubtitle)
            Spacer(Modifier.height(12.dp))

            if (vaccines.isEmpty()) {
                EmptyHint(text = s.noVaccines)
            } else {
                vaccines.forEachIndexed { index, vaccine ->
                    VaccineRow(vaccine = vaccine, onDelete = { state.removeVaccine(petId, vaccine.id) })
                    if (index != vaccines.lastIndex) Spacer(Modifier.height(8.dp))
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(
                    value = name,
                    onChange = { tfv -> name = tfv.copy(text = tfv.text.take(40)) },
                    hint = s.vaccineName,
                    modifier = Modifier.weight(1.4f),
                )
                AppTextField(
                    value = date,
                    onChange = { tfv ->
                        val formatted = formatDateInput(tfv.text)
                        date = TextFieldValue(
                            text = formatted,
                            selection = androidx.compose.ui.text.TextRange(formatted.length),
                        )
                    },
                    hint = s.datePlaceholder,
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(10.dp))
            PrimaryButton(
                text = s.addVaccine,
                leadingEmoji = "+",
                modifier = Modifier.fillMaxWidth(),
                enabled = name.text.isNotBlank(),
                onClick = {
                    if (!isValidDate(date.text)) {
                        scope.launch {
                            snackbar.showSnackbar(s.dateFormatError)
                        }
                        return@PrimaryButton
                    }
                    state.addVaccine(petId, name = name.text, date = date.text)
                    name = TextFieldValue("")
                    date = TextFieldValue(nowDate())
                },
            )
        }
    }
}

@Composable
private fun VaccineRow(vaccine: Vaccine, onDelete: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.medium)
            .background(AppColors.SurfaceElevated, AppShapes.medium)
            .border(1.dp, AppColors.CardBorder, AppShapes.medium)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(text = "💉", fontSize = 18.sp)
        Spacer(Modifier.size(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = vaccine.name, color = AppColors.OnSurface, fontWeight = FontWeight.Medium)
            Text(
                text = vaccine.date,
                color = AppColors.OnSurfaceMuted,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Text(
            text = "✕",
            color = AppColors.OnSurfaceDim,
            modifier = Modifier
                .clip(AppShapes.pill)
                .clickable(onClick = onDelete)
                .padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

// ---- Notes ----

@Composable
private fun NotesCard(petId: Long) {
    val state = LocalPetState.current
    val s = LocalAppStrings.current
    var text by remember(petId) { mutableStateOf(TextFieldValue("")) }
    val notes = state.notesFor(petId)

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            SectionHeader(
                emoji = "📝",
                title = s.notes,
                subtitle = s.notesSubtitle,
            )
            Spacer(Modifier.height(12.dp))

            AppTextField(
                value = text,
                onChange = { tfv -> text = tfv.copy(text = tfv.text.take(200)) },
                hint = s.notePlaceholder,
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(10.dp))
            PrimaryButton(
                text = s.addNote,
                leadingEmoji = "+",
                modifier = Modifier.fillMaxWidth(),
                enabled = text.text.isNotBlank(),
                onClick = {
                    state.addNote(petId, text.text)
                    text = TextFieldValue("")
                },
            )
            Spacer(Modifier.height(14.dp))
            if (notes.isEmpty()) {
                EmptyHint(text = s.noNotes)
            } else {
                notes.forEachIndexed { index, note ->
                    NoteRow(note = note, onDelete = { state.removeNote(petId, note.id) })
                    if (index != notes.lastIndex) Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun NoteRow(note: PetNote, onDelete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.medium)
            .background(AppColors.SurfaceElevated, AppShapes.medium)
            .border(1.dp, AppColors.CardBorder, AppShapes.medium)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = note.date,
                color = AppColors.Accent,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "✕",
                color = AppColors.OnSurfaceDim,
                modifier = Modifier
                    .clip(AppShapes.pill)
                    .clickable(onClick = onDelete)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(text = note.text, color = AppColors.OnSurface, style = MaterialTheme.typography.bodyMedium)
    }
}

// ---- Shared mini bits ----

@Composable
private fun SectionHeader(emoji: String, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(AppShapes.medium)
                .background(AppColors.AccentDim, AppShapes.medium),
            contentAlignment = Alignment.Center,
        ) { Text(text = emoji, fontSize = 18.sp) }
        Spacer(Modifier.size(10.dp))
        Column {
            Text(text = title, color = AppColors.OnSurface, style = MaterialTheme.typography.titleLarge)
            Text(text = subtitle, color = AppColors.OnSurfaceMuted, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun EmptyHint(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppShapes.medium)
            .background(AppColors.SurfaceMuted, AppShapes.medium)
            .border(1.dp, AppColors.Divider, AppShapes.medium)
            .padding(horizontal = 12.dp, vertical = 12.dp),
    ) {
        Text(text = text, color = AppColors.OnSurfaceMuted, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun formatDateInput(input: String): String {
    val digits = input.filter { it.isDigit() }.take(8)
    return when {
        digits.length <= 4 -> digits
        digits.length <= 6 -> "${digits.substring(0, 4)}-${digits.substring(4)}"
        else -> "${digits.substring(0, 4)}-${digits.substring(4, 6)}-${digits.substring(6)}"
    }
}

private fun isValidDate(value: String): Boolean {
    if (value.length != 10) return false
    if (value[4] != '-' || value[7] != '-') return false
    val year = value.substring(0, 4).toIntOrNull() ?: return false
    val month = value.substring(5, 7).toIntOrNull() ?: return false
    val day = value.substring(8, 10).toIntOrNull() ?: return false
    if (year !in 1900..2100) return false
    if (month !in 1..12) return false
    val maxDay = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) 29 else 28
        else -> 31
    }
    return day in 1..maxDay
}
