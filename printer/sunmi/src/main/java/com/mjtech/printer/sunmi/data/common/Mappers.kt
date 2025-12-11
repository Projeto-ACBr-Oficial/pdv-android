package com.mjtech.printer.sunmi.data.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mjtech.store.domain.print.model.ImageData

fun ImageData.toAndroidBitmap(): Bitmap? {
    val imageBytes = this.data

    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}