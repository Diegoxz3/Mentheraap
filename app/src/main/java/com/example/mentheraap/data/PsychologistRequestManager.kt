package com.example.mentheraap.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class PsychologistRequestManager {
    private val firestore = FirebaseFirestore.getInstance()

    // Enviar solicitud a la psicóloga
    suspend fun sendRequest(
        userId: String,
        username: String,
        email: String,
        reason: String,
        urgency: String,
        userAvatar: String = "🌸"
    ): Result<PsychologistRequest> {
        return try {
            val requestId = firestore.collection("psychologist_requests").document().id

            // Crear mapa con los datos para el panel web
            val requestData = hashMapOf(
                "id" to requestId,
                "userId" to userId,
                "userName" to username,
                "userEmail" to email,
                "userAvatar" to userAvatar,
                "message" to reason,
                "urgency" to urgency,
                "timestamp" to Timestamp.now(),
                "responded" to false,
                "recommendation" to "",
                "isRead" to false
            )

            firestore.collection("psychologist_requests")
                .document(requestId)
                .set(requestData)
                .await()

            val request = PsychologistRequest(
                id = requestId,
                userId = userId,
                username = username,
                email = email,
                reason = reason,
                urgency = urgency,
                createdAt = System.currentTimeMillis().toString(),
                status = "pending"
            )

            Result.success(request)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // Obtener recomendaciones del usuario
    suspend fun getUserRecommendations(userId: String): List<Recommendation> {
        return try {
            android.util.Log.d("MENTHERAAP", "=== INICIO getUserRecommendations ===")
            android.util.Log.d("MENTHERAAP", "userId buscado: $userId")

            // Primero, obtener TODOS los documentos de la colección
            val allDocs = firestore.collection("psychologist_requests")
                .get()
                .await()

            android.util.Log.d("MENTHERAAP", "Total documentos en colección: ${allDocs.size()}")

            allDocs.documents.forEach { doc ->
                val docUserId = doc.data?.get("userId") as? String
                val responded = doc.data?.get("responded") as? Boolean
                android.util.Log.d("MENTHERAAP", "Doc ${doc.id}: userId=$docUserId, responded=$responded")
            }

            // Ahora la consulta filtrada
            val snapshot = firestore.collection("psychologist_requests")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            android.util.Log.d("MENTHERAAP", "Documentos con userId=$userId: ${snapshot.size()}")

            val respondedDocs = snapshot.documents.filter {
                it.data?.get("responded") as? Boolean == true
            }

            android.util.Log.d("MENTHERAAP", "Documentos respondidos: ${respondedDocs.size}")

            respondedDocs.mapNotNull { doc ->
                try {
                    val data = doc.data ?: return@mapNotNull null
                    val recommendation = data["recommendation"] as? String ?: ""

                    android.util.Log.d("MENTHERAAP", "Procesando ${doc.id}: recommendation='$recommendation'")

                    if (recommendation.isBlank()) {
                        android.util.Log.w("MENTHERAAP", "Recommendation vacía en ${doc.id}")
                        return@mapNotNull null
                    }

                    val rec = Recommendation(
                        id = doc.id,
                        userId = data["userId"] as? String ?: "",
                        psychologistId = "psicologa",
                        psychologistName = "Psicóloga Mentheraap",
                        title = "Recomendación Personalizada",
                        message = recommendation,
                        exercises = emptyList(),
                        createdAt = (data["responseTimestamp"] as? Timestamp)?.toDate()?.time?.toString()
                            ?: (data["timestamp"] as? Timestamp)?.toDate()?.time?.toString()
                            ?: System.currentTimeMillis().toString(),
                        isRead = data["isRead"] as? Boolean ?: false,
                        type = "advice"
                    )

                    android.util.Log.d("MENTHERAAP", "✅ Recomendación creada: ${rec.id}")
                    rec
                } catch (e: Exception) {
                    android.util.Log.e("MENTHERAAP", "❌ Error procesando ${doc.id}: ${e.message}", e)
                    null
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MENTHERAAP", "❌ Error general: ${e.message}", e)
            emptyList()
        }
    }

    // Marcar recomendación como leída
    suspend fun markRecommendationAsRead(recommendationId: String): Result<Unit> {
        return try {
            firestore.collection("psychologist_requests")
                .document(recommendationId)
                .update("isRead", true)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener recomendaciones no leídas
    suspend fun getUnreadRecommendationsCount(userId: String): Int {
        return try {
            val snapshot = firestore.collection("psychologist_requests")
                .whereEqualTo("userId", userId)
                .whereEqualTo("responded", true)
                .whereEqualTo("isRead", false)
                .get()
                .await()

            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }
}