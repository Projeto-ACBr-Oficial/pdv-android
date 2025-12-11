package com.mjtech.store.domain.products.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val name: String
)