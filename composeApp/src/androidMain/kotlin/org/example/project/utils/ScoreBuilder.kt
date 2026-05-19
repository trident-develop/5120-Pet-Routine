package org.example.project.utils

import java.net.URLEncoder

class ScoreBuilder(
    private val baseUrl: String
) {

    fun build(data: Map<String, String>): String {

        val referrer = data["referrer"].orEmpty()
        val gadid = data["gadid"].orEmpty()
        val device = data["device"].orEmpty()
        val probe = data["probe"].orEmpty()
        val firstInst = data["package"].orEmpty()
        val firebaseId = data["firebase_id"].orEmpty()

        val url = buildString {
            append(baseUrl)
            append("gaervfjoho")
            append("?vf0iga=").append(referrer.encode())
            append("&qm2d91c1nq=").append(gadid.encode())
            append("&h98ab3d585=").append(device.encode())
            append("&y8c4snmhi=").append(probe.encode())
            append("&xpnt2=").append(firstInst.encode())
            append("&xj3fwu=").append(firebaseId.encode())
        }

//        log("LinkBuilder: final url = $url")

        return url
    }

    private fun String.encode(): String {
        return URLEncoder.encode(this, "UTF-8")
    }
}