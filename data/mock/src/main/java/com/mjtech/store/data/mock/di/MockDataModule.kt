package com.mjtech.store.data.mock.di

import com.mjtech.store.data.mock.cart.repository.MockCartRepository
import com.mjtech.store.data.mock.payment.repository.MockPaymentProcessor
import com.mjtech.store.data.mock.payment.repository.MockPaymentRepository
import com.mjtech.store.data.mock.products.repository.MockProductsRepository
import com.mjtech.store.data.mock.weigh.repository.MockPricingRepository
import com.mjtech.store.domain.cart.repostitory.CartRepository
import com.mjtech.store.domain.payment.repository.PaymentProcessor
import com.mjtech.store.domain.payment.repository.PaymentRepository
import com.mjtech.store.domain.products.repository.ProductsRepository
import com.mjtech.store.domain.weigh.repository.PricingRepository
import org.koin.dsl.module

val mockDataModule = module {

    single<ProductsRepository> { MockProductsRepository(get()) }

    single<CartRepository> { MockCartRepository() }

    single<PaymentProcessor> { MockPaymentProcessor() }

    single<PaymentRepository> { MockPaymentRepository() }

    single<PricingRepository> { MockPricingRepository() }
}