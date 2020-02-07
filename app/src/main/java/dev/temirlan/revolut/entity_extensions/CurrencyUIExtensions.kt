package dev.temirlan.revolut.entity_extensions

import android.content.Context
import com.blongho.country_data.World
import dev.temirlan.revolut.R
import dev.temirlan.revolut.domain.entities.Currency

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/6/20.
 */
private const val EURO_LABEL = "EUR"

fun Currency.getCountryIdentifier(): String {
    return label.substring(0, label.length - 1)
}

fun Currency.getFlagResourceId(): Int {
    return when (label) {
        EURO_LABEL -> R.drawable.eu
        else -> World.getFlagOf(getCountryIdentifier())
    }
}

fun Currency.getName(context: Context): String {
    return when (label) {
        EURO_LABEL -> context.getString(R.string.currency_name_euro)
        else -> World.getCountryFrom(getCountryIdentifier()).currency.name
    }
}