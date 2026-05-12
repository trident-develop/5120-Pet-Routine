package org.example.project.screens.pet

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.LocalAppStrings
import org.example.project.data.LocalPetState
import org.example.project.platform.BackHandler
import org.example.project.theme.AppColors
import org.example.project.theme.AppShapes
import org.example.project.ui.components.ScreenHeader

@Composable
fun PetScreen() {
    val state = LocalPetState.current
    val focusManager = LocalFocusManager.current
    val s = LocalAppStrings.current
    var addingNew by remember { mutableStateOf(false) }
    val showForm = state.pets.isEmpty() || addingNew

    // Back from "add another" returns to the pager.
    BackHandler(enabled = addingNew && state.pets.isNotEmpty()) { addingNew = false }

    val displayedPet = if (showForm) null else state.currentPet

    val showBack = addingNew && state.pets.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { focusManager.clearFocus() },
            ),
    ) {
        if (showBack) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(AppShapes.medium)
                        .background(AppColors.Surface, AppShapes.medium)
                        .clickable { addingNew = false },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "←", color = AppColors.OnSurface, fontSize = 22.sp)
                }
            }
        }
        ScreenHeader(
            eyebrow = if (state.pets.isEmpty()) s.petEyebrow
                else s.petEyebrowWithIndex(state.currentIndex + 1, state.pets.size),
            title = when {
                displayedPet != null -> displayedPet.name
                state.pets.isNotEmpty() -> s.addAnotherPet
                else -> s.petProfile
            },
            subtitle = when {
                displayedPet != null -> null
                state.pets.isNotEmpty() -> s.saveToSwipe
                else -> s.addRealPet
            },
        )
        Spacer(Modifier.height(14.dp))

        AnimatedContent(
            targetState = showForm,
            transitionSpec = {
                fadeIn(tween(280)) togetherWith fadeOut(tween(180))
            },
            label = "pet-mode",
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) { form ->
            if (form) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().imePadding(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                ) {
                    item {
                        AddPetForm(
                            onSave = { pet ->
                                state.addPet(pet)
                                addingNew = false
                            },
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    PetProfilePager(onAddNew = { addingNew = true })
                }
            }
        }
    }
}
