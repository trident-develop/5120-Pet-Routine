package org.example.project.event

sealed class FlowSignal {
    data object Continue : FlowSignal()
    data class Finish(val decision: LoadingDecision) : FlowSignal()
}