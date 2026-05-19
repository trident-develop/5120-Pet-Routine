package org.example.project.nodes

import org.example.project.event.FlowSignal
import org.example.project.event.LoadingDecision
import org.example.project.event.LoadingFlowContext
import org.example.project.event.LoadingNode

class BrokenScoreNode : LoadingNode {
    override val name: String = "BrokenScoreNode"

    override suspend fun run(ctx: LoadingFlowContext): Pair<LoadingFlowContext, FlowSignal> {

        val brokenScore = ctx.storage.getBrokenScore()

        return if (!brokenScore.isNullOrBlank()) {
//            log("$name: broken score found = $brokenScore")
            ctx.put("broken_score", brokenScore) to FlowSignal.Finish(
                LoadingDecision.OpenOtherScreen("broken_score_exists")
            )
        } else {
//            log("$name: broken score empty")
            ctx to FlowSignal.Continue
        }
    }
}