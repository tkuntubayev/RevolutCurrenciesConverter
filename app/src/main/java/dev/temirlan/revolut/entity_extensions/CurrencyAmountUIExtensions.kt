package dev.temirlan.revolut.entity_extensions

import dev.temirlan.revolut.domain.entities.CurrencyAmount
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/6/20.
 */
fun getDefaultDecimalFormat(): NumberFormat {
    val decimalFormat = DecimalFormat.getNumberInstance(Locale.US)
    decimalFormat.isGroupingUsed = false
    decimalFormat.maximumFractionDigits = 2
    return decimalFormat
}

fun CurrencyAmount.getAmount(): String {
    return getDefaultDecimalFormat().format(amount)
}