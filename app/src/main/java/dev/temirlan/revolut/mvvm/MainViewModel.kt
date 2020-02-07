package dev.temirlan.revolut.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.temirlan.revolut.domain.entities.Currency
import dev.temirlan.revolut.domain.entities.CurrencyAmount
import dev.temirlan.revolut.domain.use_cases.CurrenciesExchangeUseCase
import dev.temirlan.revolut.domain.use_cases.CurrenciesFlowUseCase
import dev.temirlan.revolut.support.task.CoroutineTask
import dev.temirlan.revolut.support.task.FlowTask
import dev.temirlan.task.Task
import dev.temirlan.task.TaskHandler
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/6/20.
 */
class MainViewModel : ViewModel(), MainContract.ViewModel, KoinComponent {

    internal var isLoadingLiveData = MutableLiveData<Boolean>()
    internal var currencyAmountsLiveData = MutableLiveData<List<CurrencyAmount>>()
    internal var exceptionLiveData = MutableLiveData<Exception?>()
    internal var isNetworkSettingsVisibleLiveData = MutableLiveData<Boolean>()

    private val currenciesFlowUseCase: CurrenciesFlowUseCase by inject()
    private val currenciesExchangeUseCase: CurrenciesExchangeUseCase by inject()

    private val taskHandler = TaskHandler()

    init {
        onRefreshed()
    }

    override fun onCleared() {
        taskHandler.cancelAll()
        super.onCleared()
    }

    override fun onRefreshed() {
        isLoadingLiveData.postValue(true)
        currencyAmountsLiveData.postValue(mutableListOf())
        exceptionLiveData.postValue(null)
        isNetworkSettingsVisibleLiveData.postValue(false)
        val currenciesFlowTask = FlowTask(
            "currenciesFlow",
            Task.Strategy.KillFirst,
            { currenciesFlowUseCase.execute() },
            {
                isLoadingLiveData.postValue(false)
                handleUpdatedCurrencies(it)
            },
            {
                isLoadingLiveData.postValue(false)
                exceptionLiveData.postValue(it)
                exceptionLiveData.postValue(null)
            }
        )
        taskHandler.handle(currenciesFlowTask)
    }

    override fun onCurrencySelected(currencyAmount: CurrencyAmount) {
        val currencyAmounts = ArrayList(currencyAmountsLiveData.value ?: listOf())
        val selectedCurrencyAmount = currencyAmounts.getOrNull(0)
        if (selectedCurrencyAmount?.currency?.label != currencyAmount.currency.label) {
            val index = currencyAmounts.indexOfFirst {
                it.currency.label == currencyAmount.currency.label
            }
            if (index != -1) {
                currencyAmounts.removeAt(index)
                currencyAmounts.add(0, currencyAmount)
                currencyAmountsLiveData.postValue(currencyAmounts)
            }
        }
    }

    override fun onCurrencyAmountEdited(currencyAmount: CurrencyAmount) {
        currencyAmountsLiveData.value?.let {
            exchangeCurrencies(currencyAmount, it.map(CurrencyAmount::currency))
        }
    }

    override fun onNetworkConnectionLost() {
        isNetworkSettingsVisibleLiveData.postValue(true)
    }

    private fun handleUpdatedCurrencies(currencies: List<Currency>) {
        val currencyAmounts = ArrayList(currencyAmountsLiveData.value ?: listOf())
        val newCurrencies = currencies.filter { currency ->
            currencyAmounts.firstOrNull { it.currency.label == currency.label } == null
        }
        if (newCurrencies.isNotEmpty()) {
            currencyAmounts.addAll(newCurrencies.map { CurrencyAmount(it, 0.0) })
            currencyAmountsLiveData.postValue(currencyAmounts)
        }

        val selectedCurrencyAmount = currencyAmountsLiveData.value?.getOrNull(0)
        val baseCurrencyAmount = when {
            selectedCurrencyAmount != null -> selectedCurrencyAmount
            currencies.isNotEmpty() -> CurrencyAmount(currencies[0], 0.0)
            else -> CurrencyAmount(Currency("", 0.0), 0.0)
        }
        exchangeCurrencies(baseCurrencyAmount, currencies)
    }

    private fun exchangeCurrencies(baseCurrencyAmount: CurrencyAmount, currencies: List<Currency>) {
        val currenciesExchangeTask = CoroutineTask(
            "currenciesExchange",
            Task.Strategy.KillFirst,
            {
                val oldCurrencyAmounts = currencyAmountsLiveData.value
                currenciesExchangeUseCase.execute(
                    baseCurrencyAmount,
                    currencies
                ).sortedWith(
                    Comparator { first, second ->
                        val oldIndexOfFirst = oldCurrencyAmounts?.indexOfFirst {
                            it.currency.label == first?.currency?.label
                        } ?: 0
                        val oldIndexOfSecond = oldCurrencyAmounts?.indexOfFirst {
                            it.currency.label == second?.currency?.label
                        } ?: 0
                        oldIndexOfFirst.compareTo(oldIndexOfSecond)
                    }
                )
            },
            { currencyAmountsLiveData.postValue(it) },
            {
                exceptionLiveData.postValue(it)
                exceptionLiveData.postValue(null)
            }
        )
        taskHandler.handle(currenciesExchangeTask)
    }
}