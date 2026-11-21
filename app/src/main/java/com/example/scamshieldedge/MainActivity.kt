
package com.example.scamshieldedge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scamshieldedge.detector.DetectionResult
import com.example.scamshieldedge.detector.RiskLevel
import com.example.scamshieldedge.detector.SmsDetector
import com.example.scamshieldedge.explain.TemplateExplainer
import com.example.scamshieldedge.explain.OnDeviceLlama
import androidx.compose.ui.platform.LocalContext

private val Navy = Color(0xFF101B35)
private val Blue = Color(0xFF4169E1)
private val LightBlue = Color(0xFFEAF0FF)
private val Background = Color(0xFFF5F7FC)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ScamShieldApp()
        }
    }
}

@Composable
fun ScamShieldApp() {
    var message by remember { mutableStateOf("") }
    var detectionResult by remember {
        mutableStateOf<DetectionResult?>(null)
    }
    var notice by remember { mutableStateOf("") }

    var modelLoading by remember { mutableStateOf(false) }
    var modelReady by remember { mutableStateOf(false) }
    var modelStatus by remember { mutableStateOf("Local AI model is not loaded.") }

    var aiGenerating by remember { mutableStateOf(false) }
    var aiExplanation by remember { mutableStateOf("") }
    var aiError by remember { mutableStateOf("") }

    val context = LocalContext.current
    val llama = remember { OnDeviceLlama(context) }

    DisposableEffect(llama) {
        onDispose {
            llama.close()
        }
    }

    val explanation = detectionResult?.let {
        TemplateExplainer.explain(it)
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "SCAMSHIELD",
                color = Blue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "EDGE",
                color = Navy,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Detect it. Explain it. Prevent it.",
                color = Color.DarkGray,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "Message Security Scanner",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Paste a suspicious SMS message below.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = message,
                        onValueChange = {
                            message = it
                            detectionResult = null
                            notice = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp),
                        placeholder = {
                            Text("Paste your SMS here...")
                        },
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (message.isBlank()) {
                                notice = "Please enter a message first."
                                detectionResult = null
                            } else {
                                detectionResult =
                                    SmsDetector.analyze(message.trim())
                                notice = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Blue
                        )
                    ) {
                        Text(
                            text = "Analyze Message",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (notice.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = notice,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LightBlue
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "SECURITY REPORT",
                        color = Blue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val report = detectionResult

                    if (report == null) {
                        Text(
                            text = "No analysis yet",
                            color = Navy,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Your security report will appear here.",
                            color = Color.DarkGray,
                            fontSize = 14.sp
                        )
                    } else {
                        val riskColor = when (report.level) {
                            RiskLevel.HIGH -> Color(0xFFC62828)
                            RiskLevel.MEDIUM -> Color(0xFFEF6C00)
                            RiskLevel.LOW -> Color(0xFF2E7D32)
                        }

                        Text(
                            text = "${report.level.name} RISK",
                            color = riskColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Risk Score: ${report.score}/100",
                            color = Navy,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "WHY WAS IT FLAGGED?",
                            color = Navy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (report.evidence.isEmpty()) {
                            Text(
                                text = "No strong warning signs detected.",
                                color = Color.DarkGray,
                                fontSize = 14.sp
                            )
                        } else {
                            report.evidence.forEach { item ->
                                Text(
                                    text = "ΓÇó ${item.id.replace('_', ' ')} (+${item.weight})",
                                    color = Color.DarkGray,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "EXPLANATION",
                            color = Navy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = explanation?.summary ?: "",
                            color = Color.DarkGray,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "ON-DEVICE AI EXPLANATION",
                            color = Navy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                modelLoading = true
                                modelStatus = "Loading local AI model..."
                                aiError = ""

                                llama.load { error ->
                                    modelLoading = false

                                    if (error == null) {
                                        modelReady = true
                                        modelStatus = "Local AI model loaded successfully."
                                    } else {
                                        modelStatus = "Model loading failed."
                                        aiError = error
                                    }
                                }
                            },
                            enabled = !modelLoading && !modelReady,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                if (modelLoading) "Loading Model..."
                                else if (modelReady) "Model Loaded"
                                else "Load Local AI Model"
                            )
                        }

                        Text(
                            text = modelStatus,
                            color = Color.DarkGray,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                aiGenerating = true
                                aiExplanation = ""
                                aiError = ""

                                val evidenceText =
                                    if (report.evidence.isEmpty()) {
                                        "No strong warning signs detected."
                                    } else {
                                        report.evidence.joinToString("\n") {
                                            "${it.description} (+${it.weight})"
                                        }
                                    }

                                llama.explain(
                                    sms = message.trim(),
                                    risk = "${report.level.name} RISK (${report.score}/100)",
                                    evidence = evidenceText
                                ) { result, error ->
                                    aiGenerating = false

                                    if (error != null) {
                                        aiError = error
                                    } else {
                                        aiExplanation = result.orEmpty()
                                    }
                                }
                            },
                            enabled = modelReady && !aiGenerating,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                if (aiGenerating) "Generating Explanation..."
                                else "Generate AI Explanation"
                            )
                        }

                        if (aiExplanation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = aiExplanation,
                                color = Navy,
                                fontSize = 14.sp
                            )
                        }

                        if (aiError.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = aiError,
                                color = Color(0xFFC62828),
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "RECOMMENDED SAFE ACTIONS",
                            color = Navy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        explanation?.actions?.forEachIndexed { index, action ->
                            Text(
                                text = "${index + 1}. $action",
                                color = Color.DarkGray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Automated estimates can be wrong. Always verify messages through official channels.",
                            color = Color.DarkGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "YOUR PRIVACY MATTERS",
                color = Navy,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "ScamShield Edge is being developed for on-device, offline message security.",
                color = Color.Gray,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "SCAMSHIELD EDGE ΓÇó VERSION 1.0",
                color = Color.Gray,
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
