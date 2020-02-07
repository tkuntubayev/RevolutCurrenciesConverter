package dev.temirlan.revolut.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.temirlan.revolut.domain.entities.Currency
import dev.temirlan.revolut.domain.entities.CurrencyAmount
import dev.temirlan.revolut.domain.use_cases.CurrenciesExchangeUseCase
import dev.temirlan.revolut.domain.use_cases.CurrenciesFlowUseCase
import dev.temirlan.revolut.support.CoroutineTask
import dev.temirlan.revolut.support.FlowTask
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
    internal var throwableLiveData = MutableLiveData<Throwable>()

    private var currencyLabelsOrder = mutableListOf<String>()

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
        currencyLabelsOrder.clear()
        currencyAmountsLiveData.postValue(mutableListOf())
        val getCurrenciesTask = FlowTask(
            "getCurrencies",
            Task.Strategy.KillFirst,
            { currenciesFlowUseCase.execute() },
            { currencies ->
                isLoadingLiveData.postValue(false)
                val newCurrencies = currencies.filterNot {
                    currencyLabelsOrder.contains(it.label)
                }
                currencyLabelsOrder.addAll(newCurrencies.map { it.label })
                val currencyAmount =
                    currencyAmountsLiveData.value?.getOrNull(0) ?: CurrencyAmount(
                        currencies[0],
                        0.0
                    )
                exchange(currencyAmount, currencies)
            },
            {
                isLoadingLiveData.postValue(false)
                throwableLiveData.postValue(it)
            }
        )
        taskHandler.handle(getCurrenciesTask)
    }

    override fun onCurrencyAmountEdited(currencyAmount: CurrencyAmount) {
        currencyAmountsLiveData.value?.let {
            exchange(currencyAmount, it.map(CurrencyAmount::currency))
        }
    }

    override fun onCurrencySelected(currencyAmount: CurrencyAmount) {
        val currencyAmountArrayList = ArrayList(currencyAmountsLiveData.value ?: mutableListOf())
        if (currencyAmountArrayList.getOrNull(0)?.currency?.label != currencyAmount.currency.label) {
            val index = currencyLabelsOrder.indexOfFirst {
                it == currencyAmount.currency.label
            }
            currencyLabelsOrder.removeAt(index)
            currencyLabelsOrder.add(0, currencyAmount.currency.label)
            sortBaseOnCurrenciesOrder(currencyAmountsLiveData.value ?: mutableListOf())
        }
    }

    private fun exchange(currencyAmount: CurrencyAmount, currencies: List<Currency>) {
        val exchangeTask = CoroutineTask(
            "exchange",
            Task.Strategy.KillFirst,
            { currenciesExchangeUseCase.execute(currencyAmount, currencies) },
            { sortBaseOnCurrenciesOrder(it) },
            { throwableLiveData.postValue(it) }
        )
        taskHandler.handle(exchangeTask)
    }

    private fun sortBaseOnCurrenciesOrder(currencyAmounts: List<CurrencyAmount>) {
        val sortTask = CoroutineTask(
            "sortCurrencyAmounts",
            Task.Strategy.KillFirst,
            {
                currencyAmounts.sortedWith(Comparator { currencyAmount1, currencyAmount2 ->
                    val indexOfFirstCurrencyAmountLabel = currencyLabelsOrder.indexOfFirst {
                        it == currencyAmount1?.currency?.label
                    }
                    val indexOfSecondCurrencyAmountLabel = currencyLabelsOrder.indexOfFirst {
                        it == currencyAmount2?.currency?.label
                    }
                    indexOfFirstCurrencyAmountLabel.compareTo(indexOfSecondCurrencyAmountLabel)
                })
            },
            { currencyAmountsLiveData.postValue(it) },
            { throwableLiveData.postValue(it) }
        )
        taskHandler.handle(sortTask)
    }
}