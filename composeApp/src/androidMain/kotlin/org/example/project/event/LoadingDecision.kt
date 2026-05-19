package org.example.project.event

sealed class LoadingDecision {
    data class OpenWebView(val url: String) : LoadingDecision()
    data class OpenOtherScreen(val reason: String) : LoadingDecision()
}