package com.mjtech.store.ui.checkout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mjtech.store.databinding.ItemPaymentMethodBinding
import com.mjtech.store.domain.payment.model.PaymentMethod

class PaymentMethodAdapter(private val viewModel: CheckoutViewModel) :
    ListAdapter<PaymentMethod, PaymentMethodAdapter.PaymentMethodViewHolder>(
        PaymentMethodDiffCallback()
    ) {

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        val method = getItem(position)
        holder.bind(method)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
        val binding = ItemPaymentMethodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PaymentMethodViewHolder(binding)
    }

    inner class PaymentMethodViewHolder(private val binding: ItemPaymentMethodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(method: PaymentMethod) {
            binding.root.text = method.name
            binding.root.setOnClickListener {
                viewModel.onPaymentMethodSelected(method.id)
            }
        }
    }

    class PaymentMethodDiffCallback : DiffUtil.ItemCallback<PaymentMethod>() {

        override fun areItemsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean {
            return oldItem.id == newItem.id && oldItem.name == newItem.name
        }
    }
}