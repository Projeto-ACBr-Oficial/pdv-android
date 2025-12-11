package com.mjtech.store

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.mjtech.acbrlib.bal.di.balancaModule
import com.mjtech.printer.sunmi.di.sunmiPrinterModule
import com.mjtech.store.data.mock.di.mockDataModule
import com.mjtech.store.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class OpenStoreApplication : Application(), SingletonImageLoader.Factory {

    private lateinit var appDir: String

    override fun onCreate() {
        super.onCreate()
        val externalDir = getExternalFilesDir(null)
        appDir = if (externalDir != null) externalDir.absolutePath else ""

        startKoin {
            androidContext(this@OpenStoreApplication)
            modules(
                appModule,
                mockDataModule,
                sunmiPrinterModule,
                balancaModule(appDir = appDir)
            )
        }
    }

    override fun newImageLoader(context: android.content.Context): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }
}