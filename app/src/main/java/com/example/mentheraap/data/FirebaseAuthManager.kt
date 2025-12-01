package com.example.mentheraap.data
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Usuario actual
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // Registrar usuario con email y contraseña
    suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        isAnonymous: Boolean,
        displayName: String,
        avatar: Int
    ): Result<User> {
        return try {
            // Crear usuario en Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Error al crear usuario")

            // Crear documento de usuario en Firestore
            val user = User(
                id = firebaseUser.uid,
                username = username,
                password = "", // No guardamos la contraseña en Firestore
                isAnonymous = isAnonymous,
                displayName = displayName,
                avatar = avatar
            )

            // Guardar en Firestore
            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(user)
                .await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login con email y contraseña
    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            // Autenticar con Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Usuario no encontrado")

            // Obtener datos del usuario desde Firestore
            val userDoc = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            val user = userDoc.toObject(User::class.java)
                ?: throw Exception("Datos de usuario no encontrados")

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login anónimo
    suspend fun loginAnonymously(): Result<User> {
        return try {
            val authResult = auth.signInAnonymously().await()
            val firebaseUser = authResult.user ?: throw Exception("Error en login anónimo")

            // Crear usuario anónimo
            val user = User(
                id = firebaseUser.uid,
                username = "Anónimo",
                password = "",
                isAnonymous = true,
                displayName = "Usuario Anónimo",
                avatar = 0
            )

            // Guardar en Firestore
            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(user)
                .await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Verificar si un username ya existe
    suspend fun usernameExists(username: String): Boolean {
        return try {
            val result = firestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .await()

            !result.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    // Cerrar sesión
    fun logout() {
        auth.signOut()
    }

    // Obtener usuario actual desde Firestore
    suspend fun getCurrentUser(): User? {
        return try {
            val firebaseUser = currentUser ?: return null

            val userDoc = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            userDoc.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
