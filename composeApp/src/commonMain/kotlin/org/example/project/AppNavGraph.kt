package org.example.project

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.example.project.navigation.Destination
import org.example.project.platform.BackHandler
import org.example.project.screens.WebViewScreen
import org.example.project.screens.explore.ExploreScreen
import org.example.project.screens.pet.PetScreen
import org.example.project.screens.play.PlayScreen
import org.example.project.screens.settings.SettingsScreen
import org.example.project.ui.components.AppScaffold
import org.example.project.ui.components.BottomNavBar

private sealed interface AppRoute {
    data object Tabs : AppRoute
    data class WebView(val url: String, val title: String) : AppRoute
}

@Composable
fun AppNavGraph() {
    var route by remember { mutableStateOf<AppRoute>(AppRoute.Tabs) }
    var current by remember { mutableStateOf<Destination>(Destination.Explore) }

    when (val r = route) {
        is AppRoute.Tabs -> TabHost(
            current = current,
            onSelect = { current = it },
            openWebView = { url, title -> route = AppRoute.WebView(url, title) },
        )
        is AppRoute.WebView -> {
            BackHandler { route = AppRoute.Tabs }
            WebViewScreen(
                url = r.url,
                title = r.title,
                onBack = { route = AppRoute.Tabs },
            )
        }
    }
}

@Composable
private fun TabHost(
    current: Destination,
    onSelect: (Destination) -> Unit,
    openWebView: (url: String, title: String) -> Unit,
) {
    BackHandler(enabled = current != Destination.Explore) {
        onSelect(Destination.Explore)
    }
    AppScaffold(
        bottomBar = {
            BottomNavBar(current = current, onSelect = onSelect)
        },
    ) {
        AnimatedContent(
            targetState = current,
            transitionSpec = {
                (fadeIn(tween(220)) + slideInVertically { it / 8 }).togetherWith(
                    fadeOut(tween(160)) + slideOutVertically { -it / 8 }
                )
            },
            label = "tab",
        ) { dest ->
            when (dest) {
                Destination.Explore -> ExploreScreen()
                Destination.Play -> PlayScreen()
                Destination.Pet -> PetScreen()
                Destination.Settings -> SettingsScreen(openWebView = openWebView)
            }
        }
    }
}
