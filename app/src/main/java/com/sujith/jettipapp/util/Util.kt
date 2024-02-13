package com.sujith.jettipapp.util

fun calculateTotalTip(billAmount: Double, tipPercentage: Int): Double {
    return if (billAmount.toString()
            .isNotEmpty() && billAmount > 1
    ) (billAmount * tipPercentage) / 100 else 0.0
}