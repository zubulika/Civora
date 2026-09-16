package com.civora.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthRepository(
    private val context: Context? = null,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val prefs: SharedPreferences? = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "civora_auth_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_IDENTIFIER = "user_identifier"
        private const val KEY_USER_UID = "user_uid"
        private const val KEY_LOGIN_TIMESTAMP = "login_timestamp"
    }

    /**
     * Emits the currently authenticated FirebaseUser, or null when logged out.
     * Reacts to real-time auth state changes.
     */
    val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        // Send initial state
        trySend(auth.currentUser)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    /**
     * True if the user is authenticated either in Firebase Auth or in local persistent session.
     * Guarantees that once logged in, the session remains active across app reboots unless manually logged out.
     */
    val isUserLoggedIn: Boolean
        get() {
            val firebaseLoggedIn = try { auth.currentUser != null } catch (_: Exception) { false }
            val localLoggedIn = prefs?.getBoolean(KEY_IS_LOGGED_IN, false) ?: false
            return firebaseLoggedIn || localLoggedIn
        }

    val savedUserIdentifier: String?
        get() = prefs?.getString(KEY_USER_IDENTIFIER, null) ?: auth.currentUser?.email

    fun saveLocalSession(identifier: String, uid: String? = null) {
        prefs?.edit()?.apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_IDENTIFIER, identifier)
            putString(KEY_USER_UID, uid ?: auth.currentUser?.uid ?: "")
            putLong(KEY_LOGIN_TIMESTAMP, System.currentTimeMillis())
            apply()
        }
    }

    fun clearLocalSession() {
        prefs?.edit()?.clear()?.apply()
    }

    /**
     * Normalizes a username or National ID into an email address.
     * e.g., "1098442190" -> "1098442190@civora.app"
     * or "user@civora.app" -> unchanged.
     */
    fun normalizeIdentifier(identifier: String): String {
        val trimmed = identifier.trim()
        return if (trimmed.contains("@")) {
            trimmed
        } else {
            val sanitized = trimmed.replace(Regex("[^a-zA-Z0-9_.]"), "").lowercase()
            if (sanitized.isNotBlank()) "$sanitized@civora.app" else "citizen@civora.app"
        }
    }

    /**
     * Signs in with either an email or national ID/username and password.
     * If user does not exist yet in Firebase Auth, automatically creates the user record.
     */
    suspend fun signIn(identifier: String, password: String): Result<FirebaseUser> {
        val email = normalizeIdentifier(identifier)
        return try {
            val user = suspendCancellableCoroutine<FirebaseUser> { cont ->
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResult ->
                        val u = authResult.user
                        if (u != null) {
                            saveLocalSession(email, u.uid)
                            cont.resume(u)
                        } else {
                            cont.resumeWith(Result.failure(Exception("Authentication succeeded but user is null.")))
                        }
                    }
                    .addOnFailureListener { error ->
                        if (error is FirebaseAuthInvalidUserException) {
                            // Account doesn't exist yet, attempt automatic sign up
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener { signUpResult ->
                                    val newUser = signUpResult.user
                                    if (newUser != null) {
                                        saveLocalSession(email, newUser.uid)
                                        cont.resume(newUser)
                                    } else {
                                        cont.resumeWith(Result.failure(Exception("Sign-up succeeded but user is null.")))
                                    }
                                }
                                .addOnFailureListener { signUpError ->
                                    cont.resumeWith(Result.failure(signUpError))
                                }
                        } else {
                            cont.resumeWith(Result.failure(error))
                        }
                    }
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Signs in anonymously for guest / demo exploration.
     */
    suspend fun signInAnonymously(): Result<FirebaseUser> {
        return try {
            val user = suspendCancellableCoroutine<FirebaseUser> { cont ->
                auth.signInAnonymously()
                    .addOnSuccessListener { authResult ->
                        val u = authResult.user
                        if (u != null) {
                            saveLocalSession(u.email ?: "guest_citizen", u.uid)
                            cont.resume(u)
                        } else {
                            cont.resumeWith(Result.failure(Exception("Anonymous login succeeded but user is null.")))
                        }
                    }
                    .addOnFailureListener { error ->
                        cont.resumeWith(Result.failure(error))
                    }
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Signs out of Firebase Authentication and clears persistent local session.
     */
    fun signOut() {
        try {
            auth.signOut()
        } catch (_: Exception) {
            // Safe sign out
        }
        clearLocalSession()
    }
}
