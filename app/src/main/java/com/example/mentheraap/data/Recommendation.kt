package com.example.mentheraap.data

import com.google.firebase.firestore.PropertyName

data class Recommendation(
    @PropertyName("id") val id: String = "",
    @PropertyName("userId") val userId: String = "",
    @PropertyName("psychologistId") val psychologistId: String = "",
    @PropertyName("psychologistName") val psychologistName: String = "",
    @PropertyName("title") val title: String = "",
    @PropertyName("message") val message: String = "",
    @PropertyName("exercises") val exercises: List<String> = emptyList(),
    @PropertyName("createdAt") val createdAt: String = "",
    @PropertyName("isRead") val isRead: Boolean = false,
    @PropertyName("type") val type: String = "advice" // advice, exercise, emergency
) {
    constructor() : this("", "", "", "", "", "", emptyList(), "", false, "advice")
}

enum class RecommendationType(val label: String, val emoji: String) {
    ADVICE("Consejo personalizado", "💭"),
    EXERCISE("Ejercicio recomendado", "🧘"),
    EMERGENCY("Atención urgente", "🚨")
}