package org.example.project.screens.play

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.Animal
import org.example.project.data.GameProgress
import org.example.project.data.LocalAppLanguage
import org.example.project.data.LocalAppStrings
import org.example.project.data.LocalPetState
import org.example.project.data.QuizRepository
import org.example.project.data.Strings
import org.example.project.data.SurvivalChoice
import org.example.project.data.SurvivalScenario
import org.example.project.theme.AppColors
import org.example.project.theme.AppShapes
import org.example.project.ui.components.AnimalEmojiArt
import org.example.project.ui.components.AnimatedCounter
import org.example.project.ui.components.AppCard
import org.example.project.ui.components.ButtonStyle
import org.example.project.ui.components.PrimaryButton
import org.example.project.ui.components.ScreenHeader

private enum class GameType(val emoji: String) {
    Quiz("🎯"),
    Survival("🌲"),
    Compare("⚖️");

    fun label(s: Strings) = when (this) {
        Quiz -> s.gameQuizFull
        Survival -> s.gameSurvivalFull
        Compare -> s.gameCompareFull
    }
    fun short(s: Strings) = when (this) {
        Quiz -> s.gameQuizShort
        Survival -> s.gameSurvivalShort
        Compare -> s.gameCompareShort
    }
    fun tagline(s: Strings) = when (this) {
        Quiz -> s.gameQuizTagline
        Survival -> s.gameSurvivalTagline
        Compare -> s.gameCompareTagline
    }
}

@Composable
fun PlayScreen() {
    var selectedGame by remember { mutableStateOf(GameType.Quiz) }
    val s = LocalAppStrings.current

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            eyebrow = s.playEyebrow,
            title = selectedGame.label(s),
            subtitle = selectedGame.tagline(s),
        )
        Spacer(Modifier.height(16.dp))
        GameTypePicker(selected = selectedGame, onSelect = { selectedGame = it })
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 12.dp),
        ) {
            AnimatedContent(
                targetState = selectedGame,
                transitionSpec = {
                    (fadeIn(tween(220)) + slideInVertically { it / 6 }).togetherWith(
                        fadeOut(tween(160)) + slideOutVertically { -it / 6 }
                    )
                },
                modifier = Modifier.fillMaxSize(),
                label = "game",
            ) { game ->
                when (game) {
                    GameType.Quiz -> QuizSection(modifier = Modifier.fillMaxSize())
                    GameType.Survival -> SurvivalSection(modifier = Modifier.fillMaxSize())
                    GameType.Compare -> CompareSection(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
private fun GameTypePicker(selected: GameType, onSelect: (GameType) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        GameType.entries.forEach { type ->
            GameTile(
                type = type,
                selected = type == selected,
                onClick = { onSelect(type) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun GameTile(
    type: GameType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor by animateColorAsState(
        if (selected) AppColors.Accent else AppColors.CardBorder,
        label = "tile-border",
    )
    val emojiScale by animateFloatAsState(
        if (selected) 1.12f else 1f,
        animationSpec = spring(stiffness = 360f, dampingRatio = 0.5f),
        label = "tile-emoji",
    )
    val tileScale by animateFloatAsState(
        if (selected) 1.02f else 1f,
        animationSpec = spring(stiffness = 380f, dampingRatio = 0.55f),
        label = "tile-scale",
    )
    val backgroundBrush = if (selected) {
        Brush.linearGradient(listOf(AppColors.Accent, AppColors.AccentSoft))
    } else {
        Brush.verticalGradient(listOf(AppColors.Surface, AppColors.SurfaceMuted))
    }
    val labelColor by animateColorAsState(
        if (selected) AppColors.BackgroundDeep else AppColors.OnSurface,
        label = "tile-fg",
    )
    val captionColor by animateColorAsState(
        if (selected) AppColors.BackgroundDeep.copy(alpha = 0.65f) else AppColors.OnSurfaceMuted,
        label = "tile-caption",
    )

    val s = LocalAppStrings.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .scale(tileScale)
            .clip(AppShapes.large)
            .background(backgroundBrush, AppShapes.large)
            .border(1.dp, borderColor, AppShapes.large)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(AppShapes.medium)
                .background(
                    if (selected) AppColors.BackgroundDeep.copy(alpha = 0.22f)
                    else AppColors.SurfaceElevated,
                    AppShapes.medium,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = type.emoji,
                fontSize = 28.sp,
                modifier = Modifier.scale(emojiScale),
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = type.short(s),
            color = labelColor,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
        )
        Text(
            text = if (selected) s.playing else s.tapToPlay,
            color = captionColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private const val QUIZ_ROUND = 10

@Composable
private fun QuizSection(modifier: Modifier = Modifier) {
    val state = LocalPetState.current
    val saved = remember { state.gameProgress("quiz") }
    val lang = LocalAppLanguage.current
    var seed by remember { mutableStateOf(saved?.seed ?: kotlin.random.Random.nextLong()) }
    val questions = remember(seed, lang) {
        QuizRepository.questions(lang).shuffled(kotlin.random.Random(seed)).take(QUIZ_ROUND)
    }
    val total = questions.size
    var index by remember(seed) { mutableStateOf(saved?.takeIf { it.seed == seed }?.index ?: 0) }
    var picked by remember(seed) { mutableStateOf<Int?>(null) }
    var score by remember(seed) { mutableStateOf(saved?.takeIf { it.seed == seed }?.score ?: 0) }
    val s = LocalAppStrings.current

    LaunchedEffect(seed, index, score) {
        state.saveGameProgress("quiz", GameProgress(seed, index, score))
    }

    AppCard(modifier = modifier, contentPadding = 18.dp) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            if (index >= total) {
                ResultsView(
                    title = s.quizComplete,
                    emoji = "🎯",
                    score = score,
                    total = total,
                    onPlayAgain = { seed = kotlin.random.Random.nextLong() },
                )
                return@Column
            }
            ProgressHeader(
                label = s.animalQuizLabel,
                step = index + 1,
                total = total,
                trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🏆", fontSize = 14.sp)
                        Spacer(Modifier.size(4.dp))
                        AnimatedCounter(
                            value = score,
                            color = AppColors.OnSurface,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
            )
            Spacer(Modifier.height(12.dp))
            AnimatedContent(
                targetState = index,
                transitionSpec = {
                    (slideInVertically { it } + fadeIn(tween(220))).togetherWith(
                        slideOutVertically { -it } + fadeOut(tween(160))
                    )
                },
                label = "quiz",
            ) { i ->
                val current = questions[i]
                Column {
                    Text(
                        text = current.prompt,
                        color = AppColors.OnSurface,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(Modifier.height(12.dp))
                    current.options.forEachIndexed { optIdx, option ->
                        QuizOption(
                            text = option,
                            state = when {
                                picked == null -> OptionState.Idle
                                optIdx == current.correctIndex -> OptionState.Correct
                                optIdx == picked -> OptionState.Wrong
                                else -> OptionState.Dim
                            },
                            onClick = {
                                if (picked == null) {
                                    picked = optIdx
                                    if (optIdx == current.correctIndex) score += 1
                                }
                            },
                        )
                        if (optIdx != current.options.lastIndex) Spacer(Modifier.height(8.dp))
                    }
                    AnimatedVisibility(visible = picked != null) {
                        Column {
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = current.explanation,
                                color = AppColors.OnSurfaceMuted,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Spacer(Modifier.height(12.dp))
                            NextActionPill(
                                text = if (i == total - 1) s.seeResults else s.nextQuestion,
                                onClick = {
                                    picked = null
                                    index = i + 1
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

private enum class OptionState { Idle, Correct, Wrong, Dim }

@Composable
private fun QuizOption(text: String, state: OptionState, onClick: () -> Unit) {
    val bg by animateColorAsState(
        targetValue = when (state) {
            OptionState.Idle -> AppColors.SurfaceElevated
            OptionState.Correct -> AppColors.Success.copy(alpha = 0.18f)
            OptionState.Wrong -> AppColors.Danger.copy(alpha = 0.18f)
            OptionState.Dim -> AppColors.SurfaceMuted
        },
        animationSpec = tween(220),
        label = "opt-bg",
    )
    val borderColor by animateColorAsState(
        targetValue = when (state) {
            OptionState.Correct -> AppColors.Success
            OptionState.Wrong -> AppColors.Danger
            else -> AppColors.CardBorder
        },
        animationSpec = tween(220),
        label = "opt-border",
    )
    val scale by animateFloatAsState(
        targetValue = when (state) {
            OptionState.Correct -> 1.02f
            OptionState.Wrong -> 0.99f
            else -> 1f
        },
        animationSpec = spring(stiffness = 380f, dampingRatio = 0.5f),
        label = "opt-scale",
    )
    Row(
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
            .clip(AppShapes.medium)
            .background(bg, AppShapes.medium)
            .border(1.dp, borderColor, AppShapes.medium)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, color = AppColors.OnSurface, modifier = Modifier.weight(1f))
        when (state) {
            OptionState.Correct -> Text("✓", color = AppColors.Success, fontWeight = FontWeight.Bold)
            OptionState.Wrong -> Text("✕", color = AppColors.Danger, fontWeight = FontWeight.Bold)
            else -> {}
        }
    }
}

private const val SURVIVAL_ROUND = 6

@Composable
private fun SurvivalSection(modifier: Modifier = Modifier) {
    val state = LocalPetState.current
    val saved = remember { state.gameProgress("survival") }
    val lang = LocalAppLanguage.current
    var seed by remember { mutableStateOf(saved?.seed ?: kotlin.random.Random.nextLong()) }
    val scenarios = remember(seed, lang) {
        QuizRepository.scenarios(lang).shuffled(kotlin.random.Random(seed)).take(SURVIVAL_ROUND)
    }
    val total = scenarios.size
    var idx by remember(seed) { mutableStateOf(saved?.takeIf { it.seed == seed }?.index ?: 0) }
    var picked by remember(seed) { mutableStateOf<SurvivalChoice?>(null) }
    var score by remember(seed) { mutableStateOf(saved?.takeIf { it.seed == seed }?.score ?: 0) }
    val s = LocalAppStrings.current

    LaunchedEffect(seed, idx, score) {
        state.saveGameProgress("survival", GameProgress(seed, idx, score))
    }

    AppCard(modifier = modifier, contentPadding = 18.dp) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            if (idx >= total) {
                ResultsView(
                    title = s.runComplete,
                    emoji = "🌲",
                    score = score,
                    total = total,
                    successLabel = s.survivedLabel,
                    onPlayAgain = { seed = kotlin.random.Random.nextLong() },
                )
                return@Column
            }
            ProgressHeader(label = s.survivalLabel, step = idx + 1, total = total, trailing = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🛡", fontSize = 14.sp)
                    Spacer(Modifier.size(4.dp))
                    AnimatedCounter(
                        value = score,
                        color = AppColors.OnSurface,
                        fontWeight = FontWeight.Bold,
                    )
                }
            })
            Spacer(Modifier.height(12.dp))
            val scenario: SurvivalScenario = scenarios[idx]
            Text(
                text = scenario.situation,
                color = AppColors.OnSurface,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(12.dp))
            scenario.choices.forEach { choice ->
                val highlighted = picked == choice
                val color = when {
                    picked == null -> AppColors.SurfaceElevated
                    highlighted && choice.survives -> AppColors.Success.copy(alpha = 0.18f)
                    highlighted -> AppColors.Danger.copy(alpha = 0.18f)
                    else -> AppColors.SurfaceMuted
                }
                val border = when {
                    !highlighted -> AppColors.CardBorder
                    choice.survives -> AppColors.Success
                    else -> AppColors.Danger
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(AppShapes.medium)
                        .background(color, AppShapes.medium)
                        .border(1.dp, border, AppShapes.medium)
                        .clickable(enabled = picked == null) {
                            picked = choice
                            if (choice.survives) score += 1
                        }
                        .padding(14.dp),
                ) {
                    Text(text = choice.text, color = AppColors.OnSurface)
                }
                Spacer(Modifier.height(8.dp))
            }
            AnimatedVisibility(visible = picked != null) {
                val choice = picked
                Column {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (choice?.survives == true) s.youSurvived else s.badCall,
                        color = if (choice?.survives == true) AppColors.Success else AppColors.Danger,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = choice?.outcome ?: "",
                        color = AppColors.OnSurfaceMuted,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.height(10.dp))
                    NextActionPill(
                        text = if (idx == total - 1) s.seeResults else s.nextScenario,
                        onClick = {
                            picked = null
                            idx += 1
                        },
                    )
                }
            }
        }
    }
}

private const val COMPARE_ROUND = 8

@Composable
private fun CompareSection(modifier: Modifier = Modifier) {
    val state = LocalPetState.current
    val saved = remember { state.gameProgress("compare") }
    val lang = LocalAppLanguage.current
    var seed by remember { mutableStateOf(saved?.seed ?: kotlin.random.Random.nextLong()) }
    val rounds = remember(seed, lang) { QuizRepository.generateCompareRounds(seed, COMPARE_ROUND, lang) }
    val total = rounds.size
    var idx by remember(seed) { mutableStateOf(saved?.takeIf { it.seed == seed }?.index ?: 0) }
    var pickedLeft by remember(seed) { mutableStateOf<Boolean?>(null) }
    var score by remember(seed) { mutableStateOf(saved?.takeIf { it.seed == seed }?.score ?: 0) }
    val s = LocalAppStrings.current

    LaunchedEffect(seed, idx, score) {
        state.saveGameProgress("compare", GameProgress(seed, idx, score))
    }

    AppCard(modifier = modifier, contentPadding = 18.dp) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            if (idx >= total) {
                ResultsView(
                    title = s.compareComplete,
                    emoji = "⚖️",
                    score = score,
                    total = total,
                    onPlayAgain = { seed = kotlin.random.Random.nextLong() },
                )
                return@Column
            }
            ProgressHeader(label = s.compareLabel, step = idx + 1, total = total, trailing = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🏆", fontSize = 14.sp)
                    Spacer(Modifier.size(4.dp))
                    AnimatedCounter(
                        value = score,
                        color = AppColors.OnSurface,
                        fontWeight = FontWeight.Bold,
                    )
                }
            })
            Spacer(Modifier.height(12.dp))
            val round = rounds[idx]
            Text(
                text = round.question,
                color = AppColors.OnSurface,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CompareCard(
                    animal = round.left,
                    state = compareState(pickedLeft, isLeft = true, isCorrect = round.winnerIsLeft),
                    onClick = {
                        if (pickedLeft == null) {
                            pickedLeft = true
                            if (round.winnerIsLeft) score += 1
                        }
                    },
                    modifier = Modifier.weight(1f),
                )
                CompareCard(
                    animal = round.right,
                    state = compareState(pickedLeft, isLeft = false, isCorrect = !round.winnerIsLeft),
                    onClick = {
                        if (pickedLeft == null) {
                            pickedLeft = false
                            if (!round.winnerIsLeft) score += 1
                        }
                    },
                    modifier = Modifier.weight(1f),
                )
            }
            AnimatedVisibility(visible = pickedLeft != null) {
                Column {
                    Spacer(Modifier.height(10.dp))
                    val correct = (pickedLeft == true) == round.winnerIsLeft
                    Text(
                        text = if (correct) s.correct else s.notQuite,
                        color = if (correct) AppColors.Success else AppColors.Danger,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    NextActionPill(
                        text = if (idx == total - 1) s.seeResults else s.nextRound,
                        onClick = {
                            pickedLeft = null
                            idx += 1
                        },
                    )
                }
            }
        }
    }
}

private enum class CompareState { Idle, Selected, Correct, Wrong, Dim }

private fun compareState(pickedLeft: Boolean?, isLeft: Boolean, isCorrect: Boolean): CompareState {
    if (pickedLeft == null) return CompareState.Idle
    val picked = (pickedLeft == isLeft)
    return when {
        picked && isCorrect -> CompareState.Correct
        picked && !isCorrect -> CompareState.Wrong
        !picked && isCorrect -> CompareState.Selected
        else -> CompareState.Dim
    }
}

@Composable
private fun CompareCard(
    animal: Animal,
    state: CompareState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor by animateColorAsState(
        targetValue = when (state) {
            CompareState.Correct -> AppColors.Success
            CompareState.Wrong -> AppColors.Danger
            CompareState.Selected -> AppColors.Accent
            else -> AppColors.CardBorder
        },
        label = "cmp-border",
    )
    val scale by animateFloatAsState(
        targetValue = when (state) {
            CompareState.Correct -> 1.04f
            CompareState.Wrong -> 0.97f
            else -> 1f
        },
        animationSpec = spring(stiffness = 380f, dampingRatio = 0.5f),
        label = "cmp-scale",
    )
    Box(
        modifier = modifier
            .scale(scale)
            .clip(AppShapes.medium)
            .background(AppColors.SurfaceElevated, AppShapes.medium)
            .border(1.dp, borderColor, AppShapes.medium)
            .clickable(onClick = onClick)
            .padding(12.dp),
    ) {
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
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "${animal.speedKmh} km/h · ${animal.weightKg} kg",
                color = AppColors.OnSurfaceMuted,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun ProgressHeader(
    label: String,
    step: Int,
    total: Int,
    trailing: @Composable () -> Unit = {},
) {
    val progress by animateFloatAsState(
        targetValue = (step - 1).coerceAtLeast(0).toFloat() / total,
        label = "progress",
    )
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                color = AppColors.Accent,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = "$step / $total",
                color = AppColors.OnSurfaceMuted,
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(Modifier.weight(1f))
            trailing()
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(AppShapes.pill)
                .background(AppColors.SurfaceMuted),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(4.dp)
                    .clip(AppShapes.pill)
                    .background(
                        Brush.horizontalGradient(listOf(AppColors.Accent, AppColors.AccentSoft)),
                    ),
            )
        }
    }
}

@Composable
private fun NextActionPill(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = AppColors.Accent,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(AppShapes.pill)
            .background(AppColors.AccentDim)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    )
}

@Composable
private fun ResultsView(
    title: String,
    emoji: String,
    score: Int,
    total: Int,
    successLabel: String? = null,
    onPlayAgain: () -> Unit,
) {
    val s = LocalAppStrings.current
    val resolvedLabel = successLabel ?: s.rightLabel
    val ratio = if (total == 0) 0f else score.toFloat() / total
    val grade = when {
        ratio >= 0.9f -> s.gradeExpert
        ratio >= 0.7f -> s.gradeSolid
        ratio >= 0.5f -> s.gradeDecent
        ratio >= 0.3f -> s.gradeRough
        else -> s.gradeBetterLuck
    }
    val gradeColor = when {
        ratio >= 0.7f -> AppColors.Success
        ratio >= 0.4f -> AppColors.Accent
        else -> AppColors.Danger
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = emoji, fontSize = 56.sp)
        Spacer(Modifier.size(8.dp))
        Text(
            text = title,
            color = AppColors.OnSurface,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.size(4.dp))
        Text(
            text = grade,
            color = gradeColor,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.size(20.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            AnimatedCounter(
                value = score,
                color = AppColors.Accent,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.displayMedium,
            )
            Text(
                text = " / $total",
                color = AppColors.OnSurfaceMuted,
                style = MaterialTheme.typography.titleLarge,
            )
        }
        Spacer(Modifier.size(4.dp))
        Text(
            text = s.scoreOutOf(score, resolvedLabel, total),
            color = AppColors.OnSurfaceMuted,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.size(24.dp))
        PrimaryButton(
            text = s.playAgain,
            leadingEmoji = "↻",
            onClick = onPlayAgain,
        )
        Spacer(Modifier.size(8.dp))
    }
}
