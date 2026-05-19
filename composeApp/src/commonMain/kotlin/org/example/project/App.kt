package org.example.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import org.example.project.data.LocalAppLanguage
import org.example.project.data.LocalAppStrings
import org.example.project.data.LocalPetState
import org.example.project.data.rememberPetState
import org.example.project.data.stringsFor
import org.example.project.platform.rememberKeyValueStorage
import org.example.project.screens.LoadingScreen
import org.example.project.screens.NoInternetScreen
import org.example.project.theme.AppTheme

//@Composable
//@Preview
//fun App() {
//    val storage = rememberKeyValueStorage()
//    val petState = rememberPetState(storage)
//
//    CompositionLocalProvider(
//        LocalPetState provides petState,
//        LocalAppStrings provides stringsFor(petState.language),
//        LocalAppLanguage provides petState.language,
//    ) {
//        AppTheme(darkMode = petState.darkMode) {
//            Gray(
//                loading = { LoadingScreen() },
//                noInternet = { NoInternetScreen(it) },
//                white = { AppNavGraph() }
//            )
//        }
//    }
//}
