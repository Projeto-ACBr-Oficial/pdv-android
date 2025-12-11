package com.mjtech.printer.sunmi.di

import com.mjtech.printer.sunmi.data.repository.SunmiPrinterRepository
import com.mjtech.printer.sunmi.data.source.SunmiPrinterManager
import com.mjtech.store.domain.print.repository.PrintRepository
import org.koin.dsl.module

val sunmiPrinterModule = module {

    single<SunmiPrinterManager> { SunmiPrinterManager.getInstance(get()) }

    single<PrintRepository> { SunmiPrinterRepository(get()) }
}