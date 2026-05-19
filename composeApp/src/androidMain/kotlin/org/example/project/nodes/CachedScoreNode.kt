package org.example.project.nodes

import org.example.project.event.FlowSignal
import org.example.project.event.LoadingDecision
import org.example.project.event.LoadingFlowContext
import org.example.project.event.LoadingNode

class CachedScoreNode : LoadingNode {
    override val name: String = "CachedScoreNode"

    override suspend fun run(ctx: LoadingFlowContext): Pair<LoadingFlowContext, FlowSignal> {

        val score = ctx.storage.getSavedScore()

        return if (!score.isNullOrBlank()) {
//            log("$name: cached score found = $score")
            ctx to FlowSignal.Finish(
                LoadingDecision.OpenWebView(score)
            )
        } else {
//            log("$name: cached score empty")
            ctx to FlowSignal.Continue
        }
    }
}