package com.example.mentheraap.data

/**
 * Fases de un ejercicio de respiración
 */
enum class BreathingPhase(val displayName: String) {
    INHALE("Inhala"),
    HOLD_IN("Retén"),
    EXHALE("Exhala"),
    HOLD_OUT("Pausa"),
    COMPLETE("Completado")
}

/**
 * Representa un ejercicio de respiración
 */
data class BreathingExercise(
    val id: String,
    val name: String,
    val description: String,
    val benefits: String,
    val duration: Int, // Duración total en segundos
    val cycles: Int, // Número de ciclos recomendados
    val inhaleDuration: Int, // Segundos de inhalación
    val holdInDuration: Int, // Segundos de retención después de inhalar
    val exhaleDuration: Int, // Segundos de exhalación
    val holdOutDuration: Int, // Segundos de pausa después de exhalar
    val difficulty: Difficulty,
    val icon: String // Emoji representativo
)

enum class Difficulty(val displayName: String) {
    BEGINNER("Principiante"),
    INTERMEDIATE("Intermedio"),
    ADVANCED("Avanzado")
}

/**
 * Catálogo de ejercicios de respiración validados científicamente
 */
object BreathingExercises {
    val exercises = listOf(
        // 1. Respiración 4-7-8 (Dr. Andrew Weil)
        BreathingExercise(
            id = "478",
            name = "Respiración 4-7-8",
            description = "Técnica del Dr. Andrew Weil para relajación profunda. " +
                    "Ideal antes de dormir o en momentos de ansiedad aguda.",
            benefits = "• Reduce ansiedad rápidamente\n" +
                    "• Ayuda a conciliar el sueño\n" +
                    "• Disminuye la frecuencia cardíaca\n" +
                    "• Calma la mente en segundos",
            duration = 76, // 4 ciclos completos
            cycles = 4,
            inhaleDuration = 4,
            holdInDuration = 7,
            exhaleDuration = 8,
            holdOutDuration = 0,
            difficulty = Difficulty.BEGINNER,
            icon = "🌙"
        ),

        // 2. Respiración Diafragmática
        BreathingExercise(
            id = "diaphragmatic",
            name = "Respiración Diafragmática",
            description = "Respiración abdominal profunda, base de todas las técnicas de relajación. " +
                    "Activa el nervio vago y calma el sistema nervioso.",
            benefits = "• Oxigenación completa\n" +
                    "• Reduce tensión muscular\n" +
                    "• Mejora la concentración\n" +
                    "• Base para mindfulness",
            duration = 120, // 10 ciclos de 12 segundos
            cycles = 10,
            inhaleDuration = 4,
            holdInDuration = 0,
            exhaleDuration = 8,
            holdOutDuration = 0,
            difficulty = Difficulty.BEGINNER,
            icon = "🫁"
        ),

        // 3. Respiración en Caja (Box Breathing)
        BreathingExercise(
            id = "box",
            name = "Respiración en Caja",
            description = "Técnica usada por Navy SEALs para mantener la calma en situaciones extremas. " +
                    "Perfecta para momentos de estrés intenso.",
            benefits = "• Control total del estrés\n" +
                    "• Mejora el enfoque mental\n" +
                    "• Equilibra el sistema nervioso\n" +
                    "• Usado por atletas de élite",
            duration = 64, // 4 ciclos de 16 segundos
            cycles = 4,
            inhaleDuration = 4,
            holdInDuration = 4,
            exhaleDuration = 4,
            holdOutDuration = 4,
            difficulty = Difficulty.INTERMEDIATE,
            icon = "⬜"
        ),

        // 4. Coherencia Cardíaca (5-5)
        BreathingExercise(
            id = "cardiac",
            name = "Coherencia Cardíaca",
            description = "Sincroniza tu corazón con tu respiración. " +
                    "6 respiraciones por minuto para máxima variabilidad cardíaca.",
            benefits = "• Reduce ansiedad crónica\n" +
                    "• Mejora variabilidad cardíaca\n" +
                    "• Equilibrio emocional\n" +
                    "• Efectos duraderos",
            duration = 300, // 5 minutos, 30 ciclos
            cycles = 30,
            inhaleDuration = 5,
            holdInDuration = 0,
            exhaleDuration = 5,
            holdOutDuration = 0,
            difficulty = Difficulty.INTERMEDIATE,
            icon = "❤️"
        ),

        // 5. Respiración Relajante Rápida
        BreathingExercise(
            id = "quick",
            name = "Respiración de Emergencia",
            description = "Para crisis de ansiedad o pánico. " +
                    "Enfócate en exhalar el doble de lo que inhalas.",
            benefits = "• Calma ataques de pánico\n" +
                    "• Efecto inmediato\n" +
                    "• Portable y discreto\n" +
                    "• Resetea el sistema nervioso",
            duration = 48, // 6 ciclos de 8 segundos
            cycles = 6,
            inhaleDuration = 3,
            holdInDuration = 0,
            exhaleDuration = 6,
            holdOutDuration = 0,
            difficulty = Difficulty.BEGINNER,
            icon = "🆘"
        ),

        // 6. Respiración Alterna (Nadi Shodhana simplificada)
        BreathingExercise(
            id = "alternate",
            name = "Respiración Equilibrada",
            description = "Basada en pranayama yóguico. " +
                    "Equilibra hemisferios cerebrales y calma la mente.",
            benefits = "• Equilibrio mental\n" +
                    "• Claridad de pensamiento\n" +
                    "• Reduce pensamientos rumiativos\n" +
                    "• Energía balanceada",
            duration = 120, // 12 ciclos de 10 segundos
            cycles = 12,
            inhaleDuration = 4,
            holdInDuration = 2,
            exhaleDuration = 4,
            holdOutDuration = 0,
            difficulty = Difficulty.ADVANCED,
            icon = "☯️"
        )
    )

    fun getById(id: String): BreathingExercise? {
        return exercises.find { it.id == id }
    }

    fun getByDifficulty(difficulty: Difficulty): List<BreathingExercise> {
        return exercises.filter { it.difficulty == difficulty }
    }
}