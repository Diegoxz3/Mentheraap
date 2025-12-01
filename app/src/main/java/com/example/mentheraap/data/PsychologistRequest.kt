package com.example.mentheraap.data

import com.google.firebase.firestore.PropertyName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class PsychologistRequest(
    @PropertyName("id") val id: String = "",
    @PropertyName("userId") val userId: String = "",
    @PropertyName("username") val username: String = "",
    @PropertyName("email") val email: String = "",
    @PropertyName("reason") val reason: String = "",
    @PropertyName("urgency") val urgency: String = "normal", // low, normal, high
    @PropertyName("createdAt") val createdAt: String = "",
    @PropertyName("status") val status: String = "pending" // pending, responded, closed
) {
    constructor() : this("", "", "", "", "", "normal", "", "pending")
}

enum class RequestUrgency(val label: String) {
    LOW("Baja - Consulta general"),
    NORMAL("Normal - Necesito orientación"),
    HIGH("Alta - Necesito ayuda urgente")
}