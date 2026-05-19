package org.example.project

import org.example.project.nodes.BrokenScoreNode
import org.example.project.nodes.CachedScoreNode
import org.example.project.nodes.DeviceNode
import org.example.project.nodes.FirebaseIdNode
import org.example.project.nodes.FirstInstNode
import org.example.project.nodes.GadidNode
import org.example.project.nodes.ProbeNode
import org.example.project.nodes.ReferrerNode
import org.example.project.nodes.ScoreAssembleNode
import org.example.project.utils.ScoreBuilder

object LoadingFlowFactory {

    fun create(baseUrl: String): LoadingFlowEngine {
        return LoadingFlowEngine(
            nodes = listOf(
                CachedScoreNode(),
                BrokenScoreNode(),
                ReferrerNode(),
                GadidNode(),
                DeviceNode(),
                ProbeNode(),
                FirstInstNode(),
                FirebaseIdNode(),
                ScoreAssembleNode(
                    scoreBuilder = ScoreBuilder(baseUrl)
                )
            )
        )
    }
}