
/**
Guardar usuarios
Registrar usuarios nuevos
Autenticar login
Recordar si hay sesión activa
Cerrar sesión
Obtener ID del usuario logueado
Generar IDs únicos

Y_todo esto usando DataStore, la tecnología moderna de Android para guardar datos.
 */


package com.example.mentherap.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
        private const val USER_PREFIX = "user_"
    }

    suspend fun saveUser(user: User) {
        context.dataStore.edit { preferences ->
            val userKey = USER_PREFIX + user.id
            val userData = "${user.username}|${user.password}|${user.isAnonymous}|${user.displayName}|${user.avatar}"
            preferences[stringPreferencesKey(userKey)] = userData
        }
    }

    suspend fun authenticateUser(username: String, password: String): User? {
        val preferences = context.dataStore.data.first()

        preferences.asMap().forEach { entry ->
            val key = entry.key
            val value = entry.value

            if (key.name.startsWith(USER_PREFIX) && value is String) {
                val parts = value.split("|")
                if (parts.size >= 5 && parts[0] == username && parts[1] == password) {
                    return User(
                        id = key.name.removePrefix(USER_PREFIX),
                        username = parts[0],
                        password = parts[1],
                        isAnonymous = parts[2].toBoolean(),
                        displayName = parts[3],
                        avatar = parts[4].toInt()
                    )
                }
            }
        }
        return null
    }

    suspend fun usernameExists(username: String): Boolean {
        val preferences = context.dataStore.data.first()

        preferences.asMap().forEach { entry ->
            val key = entry.key
            val value = entry.value

            if (key.name.startsWith(USER_PREFIX) && value is String) {
                val parts = value.split("|")
                if (parts.isNotEmpty() && parts[0] == username) {
                    return true
                }
            }
        }
        return false
    }

    suspend fun saveUserSession(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[CURRENT_USER_ID] = userId
        }
    }

    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
            preferences.remove(CURRENT_USER_ID)
        }
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    val currentUserId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[CURRENT_USER_ID]
    }

    fun generateUserId(): String {
        return UUID.randomUUID().toString()
    }
}