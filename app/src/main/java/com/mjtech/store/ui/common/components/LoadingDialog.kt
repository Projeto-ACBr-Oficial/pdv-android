package com.mjtech.store.ui.common.components

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.mjtech.store.R
import com.mjtech.store.databinding.LoadingDialogBinding

class LoadingDialog : DialogFragment() {

    lateinit var binding: LoadingDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoadingDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(false)
        setStyle(STYLE_NORMAL, R.style.StoreTransparentDialog)
        return dialog
    }

    companion object {
        const val TAG = "LoadingDialog"

        fun show(manager: FragmentManager) {
            val existingFragment = manager.findFragmentByTag(TAG)
            if (existingFragment == null) {
                LoadingDialog().show(manager, TAG)
            }
        }

        fun hide(manager: FragmentManager) {
            val existingFragment = manager.findFragmentByTag(TAG)
            if (existingFragment is DialogFragment) {
                existingFragment.dismissAllowingStateLoss()
            }
        }
    }
}