package com.mjtech.store.ui.checkout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mjtech.store.R
import com.mjtech.store.databinding.ItemInstallmentOptionBinding
import com.mjtech.store.domain.payment.model.InstallmentOption
import com.mjtech.store.ui.common.currencyFormat

class InstallmentOptionAdapter(
    private val viewModel: CheckoutViewModel
) : ListAdapter<InstallmentOption, InstallmentOptionAdapter.InstallmentOptionViewHolder>(
    InstallmentOptionDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstallmentOptionViewHolder {
        val binding = ItemInstallmentOptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InstallmentOptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InstallmentOptionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class InstallmentOptionViewHolder(
        private val binding: ItemInstallmentOptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(option: InstallmentOption) {
            val monthlyValue = option.monthlyAmount.currencyFormat()
            val totalValue = option.totalAmount.currencyFormat()

            binding.tvInstallmentCount.text =
                binding.root.context.getString(
                    R.string.installment_count_format,
                    option.count,
                    monthlyValue
                )

            binding.tvTotalAmount.text =
                binding.root.context.getString(
                    R.string.total_installment_option,
                    totalValue
                )

            binding.mainLayout.setOnClickListener {
                viewModel.onInstallmentSelected(option)
                (binding.root.context as? FragmentActivity)?.supportFragmentManager?.popBackStack()
            }
        }
    }

    class InstallmentOptionDiffCallback : DiffUtil.ItemCallback<InstallmentOption>() {
        override fun areItemsTheSame(
            oldItem: InstallmentOption,
            newItem: InstallmentOption
        ): Boolean {
            return oldItem.count == newItem.count
        }

        override fun areContentsTheSame(
            oldItem: InstallmentOption,
            newItem: InstallmentOption
        ): Boolean {
            return oldItem == newItem
        }
    }
}