package org.example.project.screens.pet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.LocalAppStrings
import org.example.project.data.LocalPetState
import org.example.project.data.Pet
import org.example.project.data.PetType
import org.example.project.platform.PlatformImage
import org.example.project.platform.rememberImagePicker
import org.example.project.theme.AppColors
import org.example.project.theme.AppShapes
import org.example.project.ui.components.AppCard
import org.example.project.ui.components.ButtonStyle
import org.example.project.ui.components.PrimaryButton

private const val NAME_MAX = 12
private const val BREED_MAX = 20
private const val AGE_MAX_DIGITS = 2
private const val WEIGHT_MAX_DIGITS = 6

private fun filterLetters(input: String, max: Int): String =
    input.filter { it.isLetter() || it.isWhitespace() }.take(max)

private fun filterDigitsNoLeadingZero(input: String, max: Int): String =
    input.filter { it.isDigit() }.trimStart('0').take(max)

@Composable
fun AddPetForm(onSave: (Pet) -> Unit) {
    val petState = LocalPetState.current
    val picker = rememberImagePicker()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val s = LocalAppStrings.current

    val name = petState.draftName
    val breed = petState.draftBreed
    val age = petState.draftAge
    val weight = petState.draftWeight
    val type = petState.draftType
    val photoPath = petState.draftPhotoPath

    val canSave = name.text.isNotBlank() &&
        age.text.toIntOrNull()?.let { it > 0 } == true &&
        weight.text.toIntOrNull()?.let { it > 0 } == true

    Box(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
                keyboardController?.hide()
            })
        },
    ) {
    AppCard(contentPadding = 18.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PhotoAvatar(
                    photoPath = photoPath,
                    fallbackEmoji = type.emoji,
                )
                Spacer(Modifier.size(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = s.addYourPet,
                        color = AppColors.OnSurface,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = s.addYourPetSubtitle,
                        color = AppColors.OnSurfaceMuted,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton(
                    text = s.takePhoto,
                    leadingEmoji = "📷",
                    style = ButtonStyle.Secondary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        picker.captureFromCamera { p -> if (p != null) petState.draftPhotoPath = p }
                    },
                )
                PrimaryButton(
                    text = s.choose,
                    leadingEmoji = "🖼",
                    style = ButtonStyle.Secondary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        picker.pickFromGallery { p -> if (p != null) petState.draftPhotoPath = p }
                    },
                )
            }

            FormField(
                label = s.name,
                value = name,
                hint = "Buddy",
                onChange = { tfv ->
                    val cleaned = filterLetters(tfv.text, NAME_MAX)
                    petState.draftName = if (cleaned != name.text)
                        TextFieldValue(cleaned, selection = androidx.compose.ui.text.TextRange(cleaned.length))
                    else tfv.copy(text = cleaned)
                },
                supporting = "${name.text.length} / $NAME_MAX",
            )
            PetTypeRow(selected = type, onSelect = { petState.draftType = it })
            FormField(
                label = s.breedOptional,
                value = breed,
                hint = "Golden Retriever",
                onChange = { tfv ->
                    val cleaned = filterLetters(tfv.text, BREED_MAX)
                    petState.draftBreed = if (cleaned != breed.text)
                        TextFieldValue(cleaned, selection = androidx.compose.ui.text.TextRange(cleaned.length))
                    else tfv.copy(text = cleaned)
                },
                supporting = "${breed.text.length} / $BREED_MAX",
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    FormField(
                        label = s.ageYrs,
                        value = age,
                        hint = "3",
                        keyboardType = KeyboardType.Text,
                        onChange = { tfv ->
                            val cleaned = filterDigitsNoLeadingZero(tfv.text, AGE_MAX_DIGITS)
                            petState.draftAge = if (cleaned != age.text)
                                TextFieldValue(cleaned, selection = androidx.compose.ui.text.TextRange(cleaned.length))
                            else tfv.copy(text = cleaned)
                        },
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    FormField(
                        label = s.weightG,
                        value = weight,
                        hint = "8400",
                        keyboardType = KeyboardType.Text,
                        onChange = { tfv ->
                            val cleaned = filterDigitsNoLeadingZero(tfv.text, WEIGHT_MAX_DIGITS)
                            petState.draftWeight = if (cleaned != weight.text)
                                TextFieldValue(cleaned, selection = androidx.compose.ui.text.TextRange(cleaned.length))
                            else tfv.copy(text = cleaned)
                        },
                    )
                }
            }

            PrimaryButton(
                text = s.savePet,
                modifier = Modifier.fillMaxWidth(),
                enabled = canSave,
                onClick = {
                    onSave(
                        Pet(
                            id = 0L,
                            name = name.text.trim(),
                            type = type,
                            breed = breed.text.trim(),
                            ageYears = age.text.toIntOrNull() ?: 0,
                            weightGrams = weight.text.toIntOrNull() ?: 0,
                            photoPath = photoPath,
                        )
                    )
                },
            )
        }
    }
    }
}

@Composable
private fun PhotoAvatar(photoPath: String?, fallbackEmoji: String) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(AppColors.AccentDim, CircleShape)
            .border(2.dp, AppColors.Accent, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (photoPath != null) {
            PlatformImage(
                path = photoPath,
                modifier = Modifier.size(72.dp).clip(CircleShape),
            )
        } else {
            Text(text = fallbackEmoji, fontSize = 30.sp)
        }
    }
}

@Composable
private fun PetTypeRow(selected: PetType, onSelect: (PetType) -> Unit) {
    val s = LocalAppStrings.current
    Column {
        Text(
            text = s.type,
            color = AppColors.OnSurfaceMuted,
            style = MaterialTheme.typography.labelLarge,
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PetType.entries.forEach { t ->
                val isSelected = t == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .clip(AppShapes.medium)
                        .background(
                            if (isSelected) AppColors.AccentDim else AppColors.SurfaceElevated,
                            AppShapes.medium,
                        )
                        .border(
                            1.dp,
                            if (isSelected) AppColors.Accent else AppColors.CardBorder,
                            AppShapes.medium,
                        )
                        .clickable { onSelect(t) },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = t.emoji, fontSize = 22.sp)
                        Text(
                            text = s.petType(t),
                            color = if (isSelected) AppColors.Accent else AppColors.OnSurfaceMuted,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: TextFieldValue,
    onChange: (TextFieldValue) -> Unit,
    hint: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    supporting: String? = null,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                color = AppColors.OnSurfaceMuted,
                style = MaterialTheme.typography.labelLarge,
            )
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
                singleLine = true,
                cursorBrush = SolidColor(AppColors.Accent),
                textStyle = LocalTextStyle.current.copy(color = AppColors.OnSurface),
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { state ->
                        if (!state.isFocused) keyboardController?.hide()
                    },
            )
        }
    }
}
