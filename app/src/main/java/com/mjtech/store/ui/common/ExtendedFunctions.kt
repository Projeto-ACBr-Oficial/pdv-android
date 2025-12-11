package com.mjtech.store.ui.common

import android.content.pm.PackageManager
import java.text.NumberFormat
import java.util.Locale

fun Double.currencyFormat(): String {
    val locale = Locale.Builder()
        .setLanguage("pt")
        .setRegion("BR")
        .build()
    return NumberFormat.getCurrencyInstance(locale).format(this)
}

fun Double.format(): String {
    val locale = Locale.Builder()
        .setLanguage("pt")
        .setRegion("BR")
        .build()
    return NumberFormat.getInstance(locale).format(this)
}

fun getAppVersionName(packageManager: PackageManager, packageName: String): String {
    try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        return if (packageInfo.versionName != null) {
            packageInfo.versionName.toString()
        } else {
            "N/A"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return "N/A"
    }
}