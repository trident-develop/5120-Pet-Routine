package org.example.project

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import org.example.project.data.LocalAppLanguage
import org.example.project.data.LocalAppStrings
import org.example.project.data.LocalPetState
import org.example.project.data.rememberPetState
import org.example.project.data.stringsFor
import org.example.project.db.ScoreStorage
import org.example.project.event.LoadingDecision
import org.example.project.platform.getBaseUrl
import org.example.project.platform.isFlowersConnected
import org.example.project.platform.rememberKeyValueStorage
import org.example.project.privacy.MainClient
import org.example.project.screens.LoadingScreen
import org.example.project.screens.NoInternetScreen
import org.example.project.theme.AppTheme

@SuppressLint("ContextCastToActivity")
@Composable
fun App(
    mainClient: MainClient,
    scoreStorage: ScoreStorage,
) {
    val storage = rememberKeyValueStorage()
    val petState = rememberPetState(storage)
    var showContent by remember { mutableStateOf(false) }
    val context = LocalContext.current as MainActivity
    var retryKey by remember { mutableIntStateOf(0) }
    val isConnected = remember(retryKey) { context.isFlowersConnected() }
    val openOtherScreen = remember { {
       showContent = true
    } }

    LaunchedEffect(Unit) {
        mainClient.setOnOpenOtherScreenCallback(openOtherScreen)
    }

    CompositionLocalProvider(
        LocalPetState provides petState,
        LocalAppStrings provides stringsFor(petState.language),
        LocalAppLanguage provides petState.language,
    ) {
        AppTheme(darkMode = petState.darkMode) {
            Crossfade(
                targetState = showContent,
                animationSpec = tween(600),
                label = "gate"
            ) { gs ->
                if (gs) {
                    AppNavGraph()
                } else {
                    if (isConnected) {
                        LoadingScreen()

                        LaunchedEffect(Unit) {
                            val engine = LoadingFlowFactory.create(getBaseUrl())

                            engine.execute(
                                context = context,
                                storage = scoreStorage
                            ).collect { decision ->
                                when (decision) {
                                    is LoadingDecision.OpenWebView -> {
//                                log("LoadingScreen: open webview = ${decision.url}")
                                        mainClient.loadUrl(decision.url)
                                    }

                                    is LoadingDecision.OpenOtherScreen -> {
//                                log("LoadingScreen: open other screen, reason = ${decision.reason}")
                                        openOtherScreen()
                                    }
                                }
                            }
                        }

                    } else {
                        NoInternetScreen {
                            retryKey++
                        }
                    }
                }
            }
        }
    }
}