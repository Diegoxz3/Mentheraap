package com.example.mentheraap.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

private val Context.journalDataStore by preferencesDataStore(name = "journal_preferences")

/**
 * Gestiona el almacenamiento de entradas del diario
 */
class JournalPreferences(private val context: Context) {

    companion object {
        private const val ENTRY_PREFIX = "journal_entry_"
    }

    /**
     * Guarda una entrada del diario
     */
    suspend fun saveEntry(entry: JournalEntry) {
        context.journalDataStore.edit { preferences ->
            val key = stringPreferencesKey(ENTRY_PREFIX + entry.id)
            // Serializamos a formato simple
            val data = serializeEntry(entry)
            preferences[key] = data
        }
    }

    /**
     * Obtiene todas las entradas del usuario
     */
    suspend fun getEntries(userId: String): List<JournalEntry> {
        val preferences = context.journalDataStore.data.first()
        val entries = mutableListOf<JournalEntry>()

        preferences.asMap().forEach { (key, value) ->
            if (key.name.startsWith(ENTRY_PREFIX) && value is String) {
                try {
                    val entry = deserializeEntry(value)
                    if (entry.userId == userId) {
                        entries.add(entry)
                    }
                } catch (e: Exception) {
                    // Ignorar entradas corruptas
                }
            }
        }

        // Ordenar por fecha, más recientes primero
        return entries.sortedByDescending { it.timestamp }
    }

    /**
     * Elimina una entrada
     */
    suspend fun deleteEntry(entryId: String) {
        context.journalDataStore.edit { preferences ->
            val key = stringPreferencesKey(ENTRY_PREFIX + entryId)
            preferences.remove(key)
        }
    }

    /**
     * Obtiene entradas de los últimos N días
     */
    suspend fun getRecentEntries(userId: String, days: Int): List<JournalEntry> {
        val allEntries = getEntries(userId)
        val cutoffDate = LocalDateTime.now().minusDays(days.toLong())

        return allEntries.filter { entry ->
            try {
                val entryDate = LocalDateTime.parse(entry.timestamp)
                entryDate.isAfter(cutoffDate)
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Serializa una entrada a String
     */
    private fun serializeEntry(entry: JournalEntry): String {
        return listOf(
            entry.id,
            entry.userId,
            entry.timestamp,
            entry.anxietyLevel.toString(),
            entry.emotions.joinToString(",") { it.name },
            entry.situation,
            entry.thoughts,
            entry.physicalSymptoms.joinToString(",") { it.name },
            entry.copingStrategies.joinToString(",") { it.name },
            entry.notes,
            entry.wasHelpful?.toString() ?: "null"
        ).joinToString("|#|")
    }

    /**
     * Deserializa un String a entrada
     */
    private fun deserializeEntry(data: String): JournalEntry {
        val parts = data.split("|#|")

        return JournalEntry(
            id = parts[0],
            userId = parts[1],
            timestamp = parts[2],
            anxietyLevel = parts[3].toInt(),
            emotions = if (parts[4].isNotEmpty())
                parts[4].split(",").map { Emotion.valueOf(it) }
            else emptyList(),
            situation = parts[5],
            thoughts = parts[6],
            physicalSymptoms = if (parts[7].isNotEmpty())
                parts[7].split(",").map { PhysicalSymptom.valueOf(it) }
            else emptyList(),
            copingStrategies = if (parts[8].isNotEmpty())
                parts[8].split(",").map { CopingStrategy.valueOf(it) }
            else emptyList(),
            notes = parts[9],
            wasHelpful = when (parts.getOrNull(10)) {
                "true" -> true
                "false" -> false
                else -> null
            }
        )
    }
}