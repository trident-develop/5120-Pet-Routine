package org.example.project.navigation

sealed class Destination(
    val id: String,
    val label: String,
    val emoji: String,
) {
    data object Explore : Destination("explore", "Explore", "🌍")
    data object Play : Destination("play", "Play", "🎮")
    data object Pet : Destination("pet", "Pet", "🐾")
    data object Settings : Destination("settings", "Settings", "⚙️")

    companion object {
        val all: List<Destination> by lazy { listOf(Explore, Play, Pet, Settings) }
    }
}
