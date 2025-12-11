package com.mjtech.store.ui.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mjtech.store.R
import com.mjtech.store.databinding.FragmentInstallmentBinding
import com.mjtech.store.domain.common.Result
import com.mjtech.store.ui.common.components.SnackbarType
import com.mjtech.store.ui.common.components.showSnackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class InstallmentFragment : Fragment() {

    private lateinit var installmentAdapter: InstallmentOptionAdapter
    private val checkoutViewModel: CheckoutViewModel by activityViewModel()
    private lateinit var binding: FragmentInstallmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInstallmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configView()
        initObservers()
    }

    private fun configView() {
        installmentAdapter = InstallmentOptionAdapter(checkoutViewModel)
        binding.recyclerInstallmentOptions.adapter = installmentAdapter

        binding.btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            checkoutViewModel.uiState.collect { uiState ->
                when (uiState.installmentsOptions) {
                    is Result.Success -> {
                        installmentAdapter.submitList(uiState.installmentsOptions.data)
                    }

                    is Result.Error -> {
                        showSnackbar(
                            binding.mainLayout, getString(R.string.error_get_installment_options),
                            SnackbarType.ERROR
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}