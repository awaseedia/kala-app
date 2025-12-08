package hu.bme.ait.kalaapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import hu.bme.ait.kalaapp.data.Result
import hu.bme.ait.kalaapp.data.model.User
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            if (result.user != null) {
                Result.Success(result.user!!)
            } else {
                Result.Error(Exception("Sign in failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun signUp(email: String, password: String, displayName: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                // Create user document in Firestore
                val userDoc = User(
                    uid = user.uid,
                    email = email,
                    displayName = displayName
                )
                usersCollection.document(user.uid).set(userDoc).await()
                Result.Success(user)
            } else {
                Result.Error(Exception("Sign up failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun getUserData(uid: String): Result<User> {
        return try {
            val snapshot = usersCollection.document(uid).get().await()
            val user = snapshot.toObject<User>()
            if (user != null) {
                Result.Success(user)
            } else {
                Result.Error(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun addToFavorites(productId: String): Result<Unit> {
        return try {
            val uid = currentUser?.uid ?: throw Exception("User not logged in")
            usersCollection.document(uid)
                .update("savedProductIds", FieldValue.arrayUnion(productId))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun removeFromFavorites(productId: String): Result<Unit> {
        return try {
            val uid = currentUser?.uid ?: throw Exception("User not logged in")
            usersCollection.document(uid)
                .update("savedProductIds", FieldValue.arrayRemove(productId))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getSavedProducts(): Result<List<String>> {
        return try {
            val uid = currentUser?.uid ?: throw Exception("User not logged in")
            val userData = getUserData(uid)
            when (userData) {
                is Result.Success -> Result.Success(userData.data.savedProductIds)
                is Result.Error -> userData
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}