
package com.example.scamshieldedge.detector

import java.net.URI

object SmsDetector {

    private val patterns = mapOf(
        Ids.OTP_REQUEST to listOf(
            "share your otp",
            "send your otp",
            "provide otp",
            "otp verification",
            "otp required",
            "tell us your otp"
        ),

        Ids.CREDENTIAL_REQUEST to listOf(
            "share your password",
            "enter your pin",
            "share your pin",
            "provide your cvv",
            "share your cvv",
            "account password"
        ),


        Ids.UPI_PAYMENT to listOf(
            "approve payment",
            "collect request",
            "upi payment",
            "pay processing fee",
            "pay registration fee",
            "pay the registration fee",
            "registration fee",
            "pay a fee",
            "send money",
            "make a payment"
        ),

        Ids.ACCOUNT_THREAT to listOf(
            "account blocked",
            "account suspended",
            "account will be blocked",
            "account will be closed",
            "bank account blocked",
            "card will be blocked"
        ),

        Ids.KYC_UPDATE to listOf(
            "update kyc",
            "kyc expired",
            "complete kyc",
            "update pan",
            "update aadhaar",
            "verify your kyc"
        ),

        Ids.PRIZE_LURE to listOf(
            "you have won",
            "claim your prize",
            "winner selected",
            "free reward",
            "cash prize",
            "lottery winner"
        ),

        Ids.URGENCY to listOf(
            "immediately",
            "urgent",
            "act now",
            "within 24 hours",
            "today only",
            "final warning",
            "last chance"
        ),

        Ids.LINK_LANGUAGE to listOf(
            "click this link",
            "verify using this link",
            "open this link",
            "tap here",
            "visit this link"
        )
    )

    private val suspiciousTlds = setOf(
        "xyz", "top", "click", "work", "buzz",
        "gq", "tk", "ml", "cf"
    )

    private val shorteners = setOf(
        "bit.ly", "tinyurl.com", "t.co",
        "is.gd", "cutt.ly", "rb.gy"
    )

    private val officialDomains = mapOf(
        "sbi" to listOf("sbi.co.in", "onlinesbi.sbi"),
        "hdfc" to listOf("hdfcbank.com"),
        "icici" to listOf("icicibank.com"),
        "axis" to listOf("axisbank.com"),
        "google" to listOf("google.com"),
        "amazon" to listOf("amazon.in", "amazon.com")
    )

    private val urlPattern =
        Regex("""(?i)\b(?:https?://|www\.)[^\s<>"']+""")

    fun analyze(message: String): DetectionResult {
        val text = message.lowercase()
        val evidence = mutableListOf<Evidence>()
        val urls = extractUrls(message)

        for ((id, keywords) in patterns) {
            if (keywords.any { text.contains(it) }) {
                val weight = when (id) {
                    Ids.OTP_REQUEST -> 35
                    Ids.CREDENTIAL_REQUEST -> 35
                    Ids.UPI_PAYMENT -> 25
                    Ids.ACCOUNT_THREAT -> 20
                    Ids.KYC_UPDATE -> 20
                    Ids.PRIZE_LURE -> 20
                    Ids.URGENCY -> 10
                    Ids.LINK_LANGUAGE -> 10
                    else -> 0
                }

                evidence.add(
                    Evidence(
                        id = id,
                        weight = weight,
                        description = "Detected suspicious SMS language"
                    )
                )
            }
        }

        for (url in urls) {
            val host = getHost(url) ?: continue

            if (host in shorteners) {
                evidence.add(
                    Evidence(
                        Ids.URL_SHORTENER,
                        20,
                        "Shortened URL detected"
                    )
                )
            }

            if (host.matches(Regex("""\d{1,3}(?:\.\d{1,3}){3}"""))) {
                evidence.add(
                    Evidence(
                        Ids.URL_IP_HOST,
                        25,
                        "URL uses an IP address"
                    )
                )
            }

            val tld = host.substringAfterLast('.', "")
            if (tld in suspiciousTlds) {
                evidence.add(
                    Evidence(
                        Ids.URL_SUSPICIOUS_TLD,
                        15,
                        "URL uses a potentially suspicious domain ending"
                    )
                )
            }

            if ('@' in url) {
                evidence.add(
                    Evidence(
                        Ids.URL_AT_SIGN,
                        20,
                        "URL contains an @ symbol"
                    )
                )
            }

            if (host.count { it == '-' } >= 3) {
                evidence.add(
                    Evidence(
                        Ids.URL_MANY_HYPHENS,
                        15,
                        "URL contains many hyphens"
                    )
                )
            }

            for ((brand, domains) in officialDomains) {
                if (brand in host && domains.none {
                        host == it || host.endsWith(".$it")
                    }
                ) {
                    evidence.add(
                        Evidence(
                            Ids.URL_BRAND_MISMATCH,
                            30,
                            "URL may imitate the $brand brand"
                        )
                    )
                }
            }
        }

        val score = evidence.sumOf { it.weight }.coerceIn(0, 100)

        val level = when {
            score >= 50 -> RiskLevel.HIGH
            score >= 25 -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        return DetectionResult(
            level = level,
            score = score,
            evidence = evidence,
            urls = urls
        )
    }

    private fun extractUrls(message: String): List<String> {
        return urlPattern.findAll(message)
            .map { it.value.trimEnd('.', ',', '!', '?', ')', ']') }
            .distinct()
            .toList()
    }

    private fun getHost(url: String): String? {
        return try {
            val normalized = if (url.startsWith("www.", true)) {
                "https://$url"
            } else {
                url
            }

            URI(normalized).host?.lowercase()?.removePrefix("www.")
        } catch (e: Exception) {
            null
        }
    }
}
