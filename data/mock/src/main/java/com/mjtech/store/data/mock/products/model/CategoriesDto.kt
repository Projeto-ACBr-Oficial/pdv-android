package com.mjtech.store.data.mock.products.model

import com.mjtech.store.domain.products.model.Category
import kotlinx.serialization.Serializable

@Serializable
internal data class CategoriesDto(
    val categories: List<Category>
)