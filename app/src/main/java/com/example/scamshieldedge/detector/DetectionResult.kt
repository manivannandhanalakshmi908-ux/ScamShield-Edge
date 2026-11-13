package com.example.scamshieldedge.detector

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH
}

data class Evidence(
    val id: String,
    val weight: Int,
    val description: String
)

data class DetectionResult(
    val level: RiskLevel,
    val score: Int,
    val evidence: List<Evidence>,
    val urls: List<String>
)

object Ids {
    const val OTP_REQUEST = "OTP_REQUEST"
    const val CREDENTIAL_REQUEST = "CREDENTIAL_REQUEST"
    const val UPI_PAYMENT = "UPI_PAYMENT"
    const val ACCOUNT_THREAT = "ACCOUNT_THREAT"
    const val KYC_UPDATE = "KYC_UPDATE"
    const val PRIZE_LURE = "PRIZE_LURE"
    const val URGENCY = "URGENCY"
    const val LINK_LANGUAGE = "LINK_LANGUAGE"
    const val URL_SHORTENER = "URL_SHORTENER"
    const val URL_IP_HOST = "URL_IP_HOST"
    const val URL_SUSPICIOUS_TLD = "URL_SUSPICIOUS_TLD"
    const val URL_AT_SIGN = "URL_AT_SIGN"
    const val URL_MANY_HYPHENS = "URL_MANY_HYPHENS"
    const val URL_BRAND_MISMATCH = "URL_BRAND_MISMATCH"
}
