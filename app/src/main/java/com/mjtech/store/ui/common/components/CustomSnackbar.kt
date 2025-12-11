package com.mjtech.store.ui.common.components

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.mjtech.store.R

enum class SnackbarType(
    @ColorRes val backgroundColor: Int,
    @ColorRes val textColor: Int
) {
    ALERT(R.color.snackbar_alert_bg, R.color.text_alert),
    ERROR(R.color.snackbar_error_bg, R.color.text_error),
    INFO(R.color.snackbar_info_bg, R.color.main_color),
    SUCCESS(R.color.snackbar_success_bg, R.color.text_success)
}

@SuppressLint("InflateParams")
fun showSnackbar(
    anchorView: View,
    message: String, type: SnackbarType = SnackbarType.INFO
) {
    val context = anchorView.context

    val snackbar = Snackbar.make(anchorView, "", Snackbar.LENGTH_SHORT)
    val snackbarView = snackbar.view

    snackbarView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))

    val inflater = LayoutInflater.from(context)
    val customLayout = inflater.inflate(R.layout.snackbar_custom, null) as LinearLayout

    val backgroundDrawable = customLayout.background as GradientDrawable
    val bgColor = ContextCompat.getColor(context, type.backgroundColor)
    val textColor = ContextCompat.getColor(context, type.textColor)

    backgroundDrawable.setColor(bgColor)

    val customTextView = customLayout.findViewById<TextView>(R.id.snackbar_text)
    customTextView.text = message
    customTextView.setTextColor(textColor)

    (snackbarView as ViewGroup).removeAllViews()
    snackbarView.addView(customLayout)

    snackbar.show()
}