package com.mjtech.printer.sunmi.data.source

import android.content.Context
import android.util.Log
import com.sunmi.printerx.PrinterSdk
import com.sunmi.printerx.PrinterSdk.Printer
import com.sunmi.printerx.PrinterSdk.PrinterListen

class SunmiPrinterManager private constructor(
    context: Context
) {

    private val TAG = "SunmiPrinter"

    private var sunmiPrinterInstance: Printer? = null

    init {
        try {
            PrinterSdk.getInstance().getPrinter(context, object : PrinterListen {
                override fun onDefPrinter(p: Printer?) {
                    sunmiPrinterInstance = p
                }

                override fun onPrinters(list: MutableList<Printer?>?) {
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    fun getPrinter(): Printer? {
        return sunmiPrinterInstance
    }

    companion object {
        @Volatile
        private var INSTANCE: SunmiPrinterManager? = null

        fun getInstance(context: Context): SunmiPrinterManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SunmiPrinterManager(context).also { INSTANCE = it }
            }
    }
}