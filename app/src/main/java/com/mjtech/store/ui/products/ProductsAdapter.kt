package com.mjtech.store.ui.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.mjtech.store.R
import com.mjtech.store.databinding.ItemProductCardBinding
import com.mjtech.store.domain.products.model.Product
import com.mjtech.store.ui.common.currencyFormat

class ProductsAdapter(
    private val onAddItemClicked: (Product) -> Unit,
    private val onRemoveItemClicked: (Product) -> Unit,
    private var quantitiesProducts: Map<String, Int> = emptyMap()
) : ListAdapter<Product, ProductsAdapter.ProductViewHolder>(ProductDiffCallback()) {

    class ProductViewHolder(
        private val binding: ItemProductCardBinding,
        private val onAddItemClicked: (Product) -> Unit,
        private val onRemoveItemClicked: (Product) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentProduct: Product? = null
        private var lastClickTime = 0L
        private val clickThreshold = 300L

        init {
            binding.btnAddItem.setOnClickListener {
                if (System.currentTimeMillis() - lastClickTime < clickThreshold) {
                    return@setOnClickListener
                }
                lastClickTime = System.currentTimeMillis()

                currentProduct?.let { product ->
                    onAddItemClicked(product)
                }
            }
            binding.btnRemoveItem.setOnClickListener {
                if (System.currentTimeMillis() - lastClickTime < clickThreshold) {
                    return@setOnClickListener
                }
                lastClickTime = System.currentTimeMillis()

                currentProduct?.let { product ->
                    onRemoveItemClicked(product)
                }
            }
        }

        fun bind(product: Product, quantityInCart: Int) {
            currentProduct = product
            binding.tvProductName.text = product.name
            binding.tvProductPrice.text = product.price.currencyFormat()

            binding.ivProductImage.load(product.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.img_placeholder)
                error(R.drawable.img_placeholder)
            }

            binding.tvItemQuantity.text = quantityInCart.toString()
            binding.btnRemoveItem.isEnabled = quantityInCart > 0
        }
    }

    fun updateQuantities(newQuantities: Map<String, Int>) {
        val oldQuantities = quantitiesProducts
        quantitiesProducts = newQuantities

        val allProductIds = oldQuantities.keys + newQuantities.keys

        val changedProductIds = allProductIds.filter { id ->
            oldQuantities[id] != newQuantities[id]
        }

        changedProductIds.forEach { productId ->
            val index = currentList.indexOfFirst { it.id == productId }
            if (index != -1) {
                notifyItemChanged(index)
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.price == newItem.price &&
                    oldItem.imageUrl == newItem.imageUrl &&
                    oldItem.categoryId == newItem.categoryId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding, onAddItemClicked, onRemoveItemClicked)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        val quantityInCart = quantitiesProducts[product.id] ?: 0
        holder.bind(product, quantityInCart)
    }
}