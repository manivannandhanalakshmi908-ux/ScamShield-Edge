package com.example.scamshieldedge.explain

import com.example.scamshieldedge.detector.DetectionResult
import com.example.scamshieldedge.detector.Ids
import com.example.scamshieldedge.detector.RiskLevel

data class Explanation(
    val summary: String,
    val actions: List<String>
)

object TemplateExplainer {

    private val reasons = mapOf(
        Ids.OTP_REQUEST to "asks you to share an OTP",
        Ids.CREDENTIAL_REQUEST to "asks for your PIN, CVV or password",
        Ids.UPI_PAYMENT to "asks you to approve a payment",
        Ids.ACCOUNT_THREAT to "threatens to block your account",
        Ids.KYC_UPDATE to "asks you to update KYC details",
        Ids.PRIZE_LURE to "promises a prize or reward",
        Ids.URGENCY to "pressures you to act quickly",
        Ids.LINK_LANGUAGE to "encourages you to open a link",
        Ids.URL_SHORTENER to "uses a shortened link",
        Ids.URL_IP_HOST to "uses an IP address in the link",
        Ids.URL_SUSPICIOUS_TLD to "uses a potentially suspicious domain ending",
        Ids.URL_AT_SIGN to "contains an @ symbol in the URL",
        Ids.URL_MANY_HYPHENS to "contains a domain with many hyphens",
        Ids.URL_BRAND_MISMATCH to "may imitate a known brand"
    )

    fun explain(result: DetectionResult): Explanation {

        val ids = result.evidence.map { it.id }.toSet()

        if (result.level == RiskLevel.LOW) {
            return Explanation(
                "No strong warning signs were found. Automated detection can still be wrong.",
                listOf("Verify uncertain messages through the official app or website.")
            )
        }

        val found = ids.mapNotNull { reasons[it] }

        val summary = if (found.isEmpty()) {
            "Suspicious indicators were detected. Review this message carefully."
        } else {
            "This message looks suspicious because it " +
                    found.take(4).joinToString("; ") + "."
        }

        val actions = mutableListOf<String>()

        if (result.urls.isNotEmpty() || Ids.LINK_LANGUAGE in ids) {
            actions.add("Do not tap suspicious links.")
        }

        if (Ids.OTP_REQUEST in ids || Ids.CREDENTIAL_REQUEST in ids) {
            actions.add("Never share your OTP, PIN, CVV or password.")
        }

        if (Ids.UPI_PAYMENT in ids) {
            actions.add("Do not approve payments you did not initiate.")
        }

        actions.add("Verify through the official app or website.")

        if (result.level == RiskLevel.HIGH) {
            actions.add("If you lost money, call 1930 or report at cybercrime.gov.in.")
        }

        return Explanation(summary, actions)
    }
}