package org.example.project.event

interface LoadingNode {
    val name: String
    suspend fun run(ctx: LoadingFlowContext): Pair<LoadingFlowContext, FlowSignal>
}