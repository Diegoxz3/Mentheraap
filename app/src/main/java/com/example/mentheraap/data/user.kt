package com.example.mentheraap.data

/**
 * Representa un usuario de la app Mentheraap
 *
 * @param id Identificador único del usuario
 * @param username Nombre de usuario para login
 * @param password Contraseña (en producción debería estar encriptada)
 * @param isAnonymous Si el usuario eligió ser anónimo
 * @param displayName Nombre a mostrar (puede ser nombre real o apodo)
 * @param avatar Identificador del avatar elegido (1-8)
 */
data class User(
    val id: String,
    val username: String,
    val password: String,
    val isAnonymous: Boolean,
    val displayName: String,
    val avatar: Int = 1 // Avatar por defecto
)

/**
 * Lista de avatares disponibles con sus emojis
 */
object Avatars {
    val list = listOf(
        "🌸" to "Flor de cerezo",
        "🌊" to "Ola tranquila",
        "🌙" to "Luna serena",
        "🌱" to "Planta en crecimiento",
        "☀️" to "Sol radiante",
        "🦋" to "Mariposa libre",
        "🌈" to "Arcoíris esperanzador",
        "⭐" to "Estrella brillante"
    )

    fun getAvatar(index: Int): String {
        return list.getOrNull(index - 1)?.first ?: list[0].first
    }

    fun getAvatarName(index: Int): String {
        return list.getOrNull(index - 1)?.second ?: list[0].second
    }
}