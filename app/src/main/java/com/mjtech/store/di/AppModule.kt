package com.mjtech.store.di

import com.mjtech.store.ui.cart.CartViewModel
import com.mjtech.store.ui.checkout.CheckoutViewModel
import com.mjtech.store.ui.products.ProductsViewModel
import com.mjtech.store.ui.weigh.WeighViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel { ProductsViewModel(get()) }

    viewModel { CartViewModel(get()) }

    viewModel { CheckoutViewModel(get(), get(), get()) }

    viewModel { WeighViewModel(get(), get(),get()) }
}