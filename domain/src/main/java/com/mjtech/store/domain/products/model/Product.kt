package com.mjtech.store.domain.products.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val categoryId: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val isAvailable: Boolean
)