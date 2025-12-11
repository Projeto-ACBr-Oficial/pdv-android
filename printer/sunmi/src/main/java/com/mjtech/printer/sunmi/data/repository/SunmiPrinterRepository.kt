package com.mjtech.printer.sunmi.data.repository

import android.graphics.Bitmap
import androidx.core.graphics.scale
import com.mjtech.printer.sunmi.data.common.toAndroidBitmap
import com.mjtech.printer.sunmi.data.source.SunmiPrinterManager
import com.mjtech.store.domain.common.Result
import com.mjtech.store.domain.print.model.ImageData
import com.mjtech.store.domain.print.model.TextPrint
import com.mjtech.store.domain.print.repository.PrintRepository
import com.sunmi.printerx.enums.Align
import com.sunmi.printerx.enums.ErrorLevel
import com.sunmi.printerx.style.BaseStyle
import com.sunmi.printerx.style.BitmapStyle
import com.sunmi.printerx.style.QrStyle
import com.sunmi.printerx.style.TextStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class SunmiPrinterRepository(private val sunmiPrinterManager: SunmiPrinterManager) :
    PrintRepository {

    private val MAX_WIDTH: Int = 384

    override fun printSimpleText(textPrint: TextPrint): Flow<Result<Unit>> = flow {
        val printer = sunmiPrinterManager.getPrinter()
        if (printer == null) {
            emit(Result.Error("Impressora não inicializada"))
        }
        try {
            val baseStyle = BaseStyle.getStyle()
                .setAlign(Align.LEFT)

            val textStyle = TextStyle
                .getStyle()
                .setAlign(Align.CENTER)

            printer!!.lineApi().initLine(baseStyle)
            printer.lineApi().printText(textPrint.text, textStyle)
            printer.lineApi().autoOut()
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error("Erro ao imprimir texto: ${e.message}"))
        }
    }

    override fun printText(linesText: List<TextPrint>): Flow<Result<Unit>> = flow {
        val printer = sunmiPrinterManager.getPrinter()
        if (printer == null) {
            emit(Result.Error("Impressora não inicializada"))
        }
        try {
            val baseStyle = BaseStyle.getStyle()
                .setAlign(Align.LEFT)

            val textStyle = TextStyle
                .getStyle()
                .setAlign(Align.CENTER)

            printer!!.lineApi().initLine(baseStyle)
            printer.lineApi().printText(linesText[0].text, textStyle)
            printer.lineApi().autoOut()
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error("Erro ao imprimir texto: ${e.message}"))
        }
    }

    override fun printQrCode(text: String): Flow<Result<Unit>> = flow {
        val printer = sunmiPrinterManager.getPrinter()
        if (printer == null) {
            emit(Result.Error("Impressora não inicializada"))
        }
        try {
            val textStyle = QrStyle.getStyle()
                .setErrorLevel(ErrorLevel.L)
                .setDot(9)
                .setAlign(Align.CENTER)
            printer!!.lineApi().printQrCode(text, textStyle)
            printer.lineApi().autoOut()
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error("Erro ao imprimir QR Code: ${e.message}"))
        }
    }

    override fun printBitmap(imageData: ImageData): Flow<Result<Unit>> = flow {
        val printer = sunmiPrinterManager.getPrinter()
        if (printer == null) {
            emit(Result.Error("Impressora não inicializada"))
        }
        try {
            val bitmap = imageData.toAndroidBitmap()

            if (bitmap != null) {
                val scaledBitmap = scaleBitmap(bitmap)

                val bmpStyle = BitmapStyle.getStyle()
                    .setAlign(Align.CENTER)

                printer!!.lineApi().printBitmap(scaledBitmap, bmpStyle)
                printer.lineApi().autoOut()
                emit(Result.Success(Unit))
            } else {
                emit(Result.Error("Erro ao carregar imagem"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Erro ao imprimir imagem: ${e.message}"))
        }
    }

    private fun scaleBitmap(bitmap: Bitmap): Bitmap {
        if (bitmap.width <= MAX_WIDTH) {
            return bitmap
        }
        val newHeight = bitmap.height * MAX_WIDTH / bitmap.width
        return bitmap.scale(MAX_WIDTH, newHeight)
    }
}