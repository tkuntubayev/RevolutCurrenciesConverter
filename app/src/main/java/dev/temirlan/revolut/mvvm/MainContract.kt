package dev.temirlan.revolut.mvvm

import dev.temirlan.revolut.domain.entities.CurrencyAmount

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/6/20.
 */
interface MainContract {

    interface View {
        fun showLoading()

        fun hideLoading()

        fun setCurrencyAmounts(currencyAmounts: List<CurrencyAmount>)

        fun showCommonError(exception: Exception?)

        fun showNetworkSettingsDialog()

        fun hideNetworkSettingsDialog()
    }

    interface ViewModel {
        fun onRefreshed()

        fun onCurrencySelected(currencyAmount: CurrencyAmount)

        fun onCurrencyAmountEdited(currencyAmount: CurrencyAmount)

        fun onNetworkConnectionLost()
    }
}