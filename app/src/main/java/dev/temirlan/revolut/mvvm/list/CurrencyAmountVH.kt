package dev.temirlan.revolut.mvvm.list

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import dev.temirlan.revolut.R
import dev.temirlan.revolut.domain.entities.CurrencyAmount
import dev.temirlan.revolut.entity_extensions.*
import kotlinx.android.synthetic.main.vh_currency_amount.view.*


/**
 * Created by Temirlan Kuntubayev <t.me/tkuntubaev> on 2/6/20.
 */
class CurrencyAmountVH(
    parent: ViewGroup,
    private val onSelect: (CurrencyAmount) -> Unit,
    private val onAmountEdit: (CurrencyAmount) -> Unit
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.vh_currency_amount, parent, false)
) {
    private lateinit var currencyAmount: CurrencyAmount

    private val amountTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            text?.let {
                val firstChar = text.getOrNull(0)
                val secondChar = text.getOrNull(1)
                val isSecondCharIsDigit = secondChar?.toString()?.matches(Regex("\\d+(?:\\.\\d+)?"))
                var amountString = text.toString()
                when {
                    firstChar == '0' && isSecondCharIsDigit == true -> {
                        amountString = secondChar.toString()
                    }
                    amountString.length > MAX_AMOUNT_INPUT_LENGTH -> {
                        amountString = amountString.substring(0, MAX_AMOUNT_INPUT_LENGTH)
                    }
                }

                setAmount(amountString)
                currencyAmount.amount = try {
                    getDefaultDecimalFormat().parse(amountString).toDouble()
                } catch (e: Exception) {
                    0.0
                }
                onAmountEdit(currencyAmount)
            }
        }
    }

    fun onCreate() {
        with(itemView) {
            etAmount.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    onSelect(currencyAmount)
                }
            }

            setOnClickListener {
                onSelect(currencyAmount)
                etAmount.requestFocus()
                val inputMethodManager: InputMethodManager = context
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(etAmount, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    fun render(
        currencyAmount: CurrencyAmount
    ) {
        this.currencyAmount = currencyAmount
        with(itemView) {
            ivFlag.setImageResource(currencyAmount.currency.getFlagResourceId())

            tvCurrencyLabel.text = currencyAmount.currency.label
            tvCurrencyName.text = currencyAmount.currency.getName(context)
        }
        setAmount(currencyAmount.getAmount())
    }

    private fun setAmount(amount: String) {
        with(itemView) {
            etAmount?.removeTextChangedListener(amountTextWatcher)
            etAmount?.setText(amount)
            etAmount?.setSelection(amount.length)
            etAmount?.addTextChangedListener(amountTextWatcher)
        }
    }
}