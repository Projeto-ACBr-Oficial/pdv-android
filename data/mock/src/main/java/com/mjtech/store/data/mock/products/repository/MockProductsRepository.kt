package com.mjtech.store.data.mock.products.repository

import android.content.Context
import android.util.Log
import com.mjtech.store.data.common.AppJson
import com.mjtech.store.data.mock.products.model.CategoriesDto
import com.mjtech.store.data.mock.products.model.ProductsDto
import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.products.model.Category
import com.mjtech.store.domain.products.model.Product
import com.mjtech.store.domain.products.repository.ProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

internal class MockProductsRepository(private val context: Context) : ProductsRepository {

    private val TAG = "MockProductsRepository"

    override fun getCategories(): Flow<Result<List<Category>>> = flow {
        emit(Result.Loading)
        val fileName = "mock_categories.json"

        try {
            val jsonString = loadJsonFromAssets(fileName)
            if (jsonString == null) {
                Log.e(TAG, "Arquivo mock não encontrado para: $fileName.")
                emit(Result.Error("Erro ao carregar categorias."))
            } else {
                val json = AppJson.decodeFromString<CategoriesDto>(jsonString)
                emit(Result.Success(json.categories))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao carregar categorias: ${e.localizedMessage}", e)
            emit(Result.Error("${e.localizedMessage}"))
        }
    }

    override fun getProducts(): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        val fileName = "mock_products.json"

        try {
            val jsonString = loadJsonFromAssets(fileName)
            if (jsonString == null) {
                Log.e(TAG, "Arquivo mock não encontrado para: $fileName.")
                emit(Result.Error("Erro ao carregar produtos."))
            } else {
                val json = AppJson.decodeFromString<ProductsDto>(jsonString)
                emit(Result.Success(json.products))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao carregar produtos: ${e.localizedMessage}", e)
            emit(Result.Error("${e.localizedMessage}"))
        }
    }

    private fun loadJsonFromAssets(fileName: String): String? {
        return try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            Log.e(
                TAG,
                "Erro ao ler arquivo json '$fileName': ${e.localizedMessage}",
                e
            )
            null
        }
    }
}