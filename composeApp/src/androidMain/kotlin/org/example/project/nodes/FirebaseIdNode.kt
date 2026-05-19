package org.example.project.nodes

import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import kotlinx.coroutines.tasks.await
import org.example.project.event.FlowSignal
import org.example.project.event.LoadingFlowContext
import org.example.project.event.LoadingNode

class FirebaseIdNode : LoadingNode {

    override val name: String = "FirebaseIdNode"

    override suspend fun run(
        ctx: LoadingFlowContext
    ): Pair<LoadingFlowContext, FlowSignal> {

        val firebaseId = loadFirebaseId()

        return ctx.put("firebase_id", firebaseId) to FlowSignal.Continue
    }

    private suspend fun loadFirebaseId(): String {
        return runCatching {
            Firebase.analytics.appInstanceId.await()
        }.getOrNull() ?: "null"
    }
}