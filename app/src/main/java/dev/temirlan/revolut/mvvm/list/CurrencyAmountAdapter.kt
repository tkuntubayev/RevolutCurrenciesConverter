package dev.temirlan.revolut.mvvm.list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.temirlan.revolut.domain.entities.CurrencyAmount
import dev.temirlan.revolut.entity_extensions.getAmount


/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/6/20.
 */
class CurrencyAmountAdapter(
    private var currencyAmounts: List<CurrencyAmount>,
    private val onSelect: (CurrencyAmount) -> Unit,
    private val onAmountEdit: (CurrencyAmount) -> Unit
) : RecyclerView.Adapter<CurrencyAmountVH>() {

    inner class DiffUtilCallback(
        private val oldList: List<CurrencyAmount>,
        private val newList: List<CurrencyAmount>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].currency.label == newList[newItemPosition].currency.label
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldCurrencyAmount = oldList[oldItemPosition]
            val newCurrencyAmount = newList[newItemPosition]
            return oldCurrencyAmount.getAmount() == newCurrencyAmount.getAmount()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyAmountVH {
        return CurrencyAmountVH(parent, onSelect, onAmountEdit)
    }

    override fun onBindViewHolder(holder: CurrencyAmountVH, position: Int) {
        holder.render(currencyAmounts[position])
    }

    override fun getItemCount(): Int {
        return currencyAmounts.size
    }

    fun update(newCurrencyAmounts: List<CurrencyAmount>) {
        val diffUtilCallback = DiffUtilCallback(currencyAmounts, newCurrencyAmounts)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtilCallback)
        this.currencyAmounts = newCurrencyAmounts
        diffUtilResult.dispatchUpdatesTo(this)
    }
}