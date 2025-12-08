package hu.bme.ait.kalaapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import hu.bme.ait.kalaapp.data.Result
import hu.bme.ait.kalaapp.data.model.Product
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()
    private val productsCollection = db.collection("products")

    suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            val snapshot = productsCollection.get().await()
            val products = snapshot.documents.mapNotNull { it.toObject<Product>() }
            Result.Success(products)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getFeaturedProduct(): Result<Product> {
        return try {
            val snapshot = productsCollection
                .whereEqualTo("isFeatured", true)
                .limit(1)
                .get()
                .await()

            val product = snapshot.documents.firstOrNull()?.toObject<Product>()
            if (product != null) {
                Result.Success(product)
            } else {
                Result.Error(Exception("No featured product found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getProductsByBrand(brandId: String): Result<List<Product>> {
        return try {
            val snapshot = productsCollection
                .whereEqualTo("brandId", brandId)
                .get()
                .await()
            val products = snapshot.documents.mapNotNull { it.toObject<Product>() }
            Result.Success(products)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getProductById(productId: String): Result<Product> {
        return try {
            val snapshot = productsCollection.document(productId).get().await()
            val product = snapshot.toObject<Product>()
            if (product != null) {
                Result.Success(product)
            } else {
                Result.Error(Exception("Product not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun searchProducts(query: String): Result<List<Product>> {
        return try {
            val snapshot = productsCollection.get().await()
            val allProducts = snapshot.documents.mapNotNull { it.toObject<Product>() }

            // Client-side filtering (Firestore doesn't support full-text search well)
            val filtered = allProducts.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true) ||
                        it.brandName.contains(query, ignoreCase = true)
            }

            Result.Success(filtered)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getProductsByCategory(category: String): Result<List<Product>> {
        return try {
            val snapshot = productsCollection
                .whereEqualTo("category", category)
                .get()
                .await()
            val products = snapshot.documents.mapNotNull { it.toObject<Product>() }
            Result.Success(products)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}