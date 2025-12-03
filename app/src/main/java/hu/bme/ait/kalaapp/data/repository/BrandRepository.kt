package hu.bme.ait.kalaapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import hu.bme.ait.kalaapp.data.Result
import  hu.bme.ait.kalaapp.data.model.Brand
import kotlinx.coroutines.tasks.await

class BrandRepository {
    private val db = FirebaseFirestore.getInstance()
    private val brandsCollection = db.collection("brands")

    suspend fun getAllBrands(): Result<List<Brand>> {
        return try {
            val snapshot = brandsCollection.get().await()
            val brands = snapshot.documents.mapNotNull { it.toObject<Brand>() }
            Result.Success(brands)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getBrandById(brandId: String): Result<Brand> {
        return try {
            val snapshot = brandsCollection.document(brandId).get().await()
            val brand = snapshot.toObject<Brand>()
            if (brand != null) {
                Result.Success(brand)
            } else {
                Result.Error(Exception("Brand not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}