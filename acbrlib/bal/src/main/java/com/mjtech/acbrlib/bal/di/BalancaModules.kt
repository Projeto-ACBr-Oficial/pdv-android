package com.mjtech.acbrlib.bal.di

import com.mjtech.acbrlib.bal.data.repository.AcbrLibBalRepository
import com.mjtech.acbrlib.bal.data.source.ACBrLibBALManager
import com.mjtech.store.domain.weigh.repository.ScaleRepository
import org.koin.dsl.module

fun balancaModule(appDir: String) = module {

    single<ACBrLibBALManager> { ACBrLibBALManager.getInstance(appDir) }

    single<ScaleRepository> { AcbrLibBalRepository(acbrLibBal = get()) }
}