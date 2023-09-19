package com.anr.recording.model

import java.io.Serializable


enum class CallType {
    INCOMING,
    OUTGOING,
    MISSED
}


data class RegisteredCall(
    val id: Int,
    val mobileNumber: String,
    val callType: CallType,
    val duration: String,
): Serializable



