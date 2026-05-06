package org.example.project.platform

data class LegalLink(val title: String, val url: String)

expect fun platformLegalLinks(): List<LegalLink>
