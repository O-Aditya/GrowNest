package com.adityacode.grownest.utils

import android.content.Context
import android.widget.Toast

class Extensions {
    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}