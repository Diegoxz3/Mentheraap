package com.example.mentheraap.data


import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Emociones principales basadas en la rueda emocional
 */
enum class Emotion(val displayName: String, val color: String, val emoji: String) {
    // Emociones relacionadas con ansiedad
    ANXIOUS("Ansioso/a", "#FF6B6B", "😰"),
    WORRIED("Preocupado/a", "#FFA07A", "😟"),
    OVERWHELMED("Abrumado/a", "#FF8C94", "😵"),
    PANICKED("En pánico", "#FF4757", "😱"),
    NERVOUS("Nervioso/a", "#FFB347", "😬"),

    // Emociones negativas comunes
    SAD("Triste", "#4A90E2", "😢"),
    ANGRY("Enojado/a", "#E74C3C", "😠"),
    FRUSTRATED("Frustrado/a", "#E67E22", "😤"),
    TIRED("Cansado/a", "#95A5A6", "😴"),
    LONELY("Solo/a", "#6C5CE7", "😔"),

    // Emociones positivas
    CALM("Calmado/a", "#26C281", "😌"),
    HAPPY("Feliz", "#F1C40F", "😊"),
    GRATEFUL("Agradecido/a", "#A29BFE", "🙏"),
    HOPEFUL("Esperanzado/a", "#74B9FF", "🌟"),
    PEACEFUL("En paz", "#55EFC4", "☮️"),

    // Neutras/Complejas
    CONFUSED("Confundido/a", "#B8A4D3", "😕"),
    NUMB("Entumecido/a", "#95A5A6", "😶"),
    MIXED("Sentimientos mixtos", "#DDA15E", "🌀")
}

/**
 * Síntomas físicos comunes de ansiedad
 */
enum class PhysicalSymptom(val displayName: String) {
    RACING_HEART("Corazón acelerado"),
    CHEST_TIGHTNESS("Presión en el pecho"),
    SHORTNESS_OF_BREATH("Falta de aire"),
    SWEATING("Sudoración"),
    TREMBLING("Temblor"),
    NAUSEA("Náuseas"),
    DIZZINESS("Mareo"),
    HEADACHE("Dolor de cabeza"),
    TENSION("Tensión muscular"),
    FATIGUE("Fatiga"),
    STOMACH_ISSUES("Malestar estomacal"),
    NONE("Sin síntomas físicos")
}

/**
 * Estrategias de afrontamiento
 */
enum class CopingStrategy(val displayName: String, val emoji: String) {
    BREATHING("Ejercicios de respiración", "🌬️"),
    MEDITATION("Meditación", "🧘"),
    EXERCISE("Ejercicio físico", "🏃"),
    TALKING("Hablar con alguien", "💬"),
    WRITING("Escribir/Diario", "✍️"),
    MUSIC("Escuchar música", "🎵"),
    NATURE("Contacto con naturaleza", "🌳"),
    REST("Descansar/Dormir", "😴"),
    DISTRACTION("Distracción saludable", "🎮"),
    PROFESSIONAL_HELP("Ayuda profesional", "👨‍⚕️"),
    MEDICATION("Medicación", "💊"),
    NONE("No tomé ninguna acción", "❌")
}

/**
 * Entrada del diario emocional
 */
data class JournalEntry(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val timestamp: String, // ISO 8601 format
    val anxietyLevel: Int, // 1-10
    val emotions: List<Emotion>,
    val situation: String, // Qué estaba pasando
    val thoughts: String, // Qué pensaste
    val physicalSymptoms: List<PhysicalSymptom>,
    val copingStrategies: List<CopingStrategy>,
    val notes: String, // Notas adicionales libres
    val wasHelpful: Boolean? = null // Si las estrategias ayudaron
) {
    fun getFormattedDate(): String {
        return try {
            val dateTime = LocalDateTime.parse(timestamp)
            dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))
        } catch (e: Exception) {
            timestamp
        }
    }

    fun getFormattedDateShort(): String {
        return try {
            val dateTime = LocalDateTime.parse(timestamp)
            dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yy"))
        } catch (e: Exception) {
            timestamp
        }
    }

    fun getDayOfWeek(): String {
        return try {
            val dateTime = LocalDateTime.parse(timestamp)
            dateTime.format(DateTimeFormatter.ofPattern("EEEE"))
        } catch (e: Exception) {
            ""
        }
    }
}

/**
 * Estadísticas del diario
 */
data class JournalStats(
    val totalEntries: Int,
    val averageAnxietyLevel: Float,
    val mostCommonEmotion: Emotion?,
    val mostCommonSymptom: PhysicalSymptom?,
    val mostUsedStrategy: CopingStrategy?,
    val daysTracked: Int,
    val currentStreak: Int,
    val bestWeek: String,
    val improvementTrend: String // "improving", "stable", "worsening"
)

/**
 * Helper para crear entradas
 */
object JournalHelper {
    fun createEntry(
        userId: String,
        anxietyLevel: Int,
        emotions: List<Emotion>,
        situation: String,
        thoughts: String,
        physicalSymptoms: List<PhysicalSymptom>,
        copingStrategies: List<CopingStrategy>,
        notes: String,
        wasHelpful: Boolean? = null
    ): JournalEntry {
        return JournalEntry(
            userId = userId,
            timestamp = LocalDateTime.now().toString(),
            anxietyLevel = anxietyLevel,
            emotions = emotions,
            situation = situation,
            thoughts = thoughts,
            physicalSymptoms = physicalSymptoms,
            copingStrategies = copingStrategies,
            notes = notes,
            wasHelpful = wasHelpful
        )
    }

    fun calculateStats(entries: List<JournalEntry>): JournalStats {
        if (entries.isEmpty()) {
            return JournalStats(
                totalEntries = 0,
                averageAnxietyLevel = 0f,
                mostCommonEmotion = null,
                mostCommonSymptom = null,
                mostUsedStrategy = null,
                daysTracked = 0,
                currentStreak = 0,
                bestWeek = "N/A",
                improvementTrend = "stable"
            )
        }

        val avgAnxiety = entries.map { it.anxietyLevel }.average().toFloat()

        val emotionCounts = entries
            .flatMap { it.emotions }
            .groupingBy { it }
            .eachCount()
        val mostCommonEmotion = emotionCounts.maxByOrNull { it.value }?.key

        val symptomCounts = entries
            .flatMap { it.physicalSymptoms }
            .groupingBy { it }
            .eachCount()
        val mostCommonSymptom = symptomCounts.maxByOrNull { it.value }?.key

        val strategyCounts = entries
            .flatMap { it.copingStrategies }
            .groupingBy { it }
            .eachCount()
        val mostUsedStrategy = strategyCounts.maxByOrNull { it.value }?.key

        // Calcular tendencia (últimas 7 vs anteriores 7)
        val recentEntries = entries.takeLast(7)
        val olderEntries = entries.dropLast(7).takeLast(7)

        val recentAvg = recentEntries.map { it.anxietyLevel }.average()
        val olderAvg = if (olderEntries.isNotEmpty())
            olderEntries.map { it.anxietyLevel }.average()
        else recentAvg

        val trend = when {
            recentAvg < olderAvg - 1 -> "improving"
            recentAvg > olderAvg + 1 -> "worsening"
            else -> "stable"
        }

        return JournalStats(
            totalEntries = entries.size,
            averageAnxietyLevel = avgAnxiety,
            mostCommonEmotion = mostCommonEmotion,
            mostCommonSymptom = mostCommonSymptom,
            mostUsedStrategy = mostUsedStrategy,
            daysTracked = entries.distinctBy { it.getFormattedDateShort() }.size,
            currentStreak = 0, // Simplificado
            bestWeek = "Esta semana",
            improvementTrend = trend
        )
    }
}