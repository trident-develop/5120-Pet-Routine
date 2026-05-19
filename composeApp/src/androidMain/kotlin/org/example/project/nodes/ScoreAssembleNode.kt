package org.example.project.nodes

import org.example.project.event.FlowSignal
import org.example.project.event.LoadingDecision
import org.example.project.event.LoadingFlowContext
import org.example.project.event.LoadingNode
import org.example.project.utils.ScoreBuilder

class ScoreAssembleNode(
    private val scoreBuilder: ScoreBuilder
) : LoadingNode {

    override val name: String = "LinkAssembleNode"

    override suspend fun run(ctx: LoadingFlowContext): Pair<LoadingFlowContext, FlowSignal> {

        val url = scoreBuilder.build(ctx.data)

//        log("$name: link ready = $url")

        return ctx.put("final_url", url) to FlowSignal.Finish(
            LoadingDecision.OpenWebView(url)
        )
    }
}