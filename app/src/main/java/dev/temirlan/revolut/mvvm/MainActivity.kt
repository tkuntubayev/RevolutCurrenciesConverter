package dev.temirlan.revolut.mvvm

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import dev.temirlan.revolut.R
import dev.temirlan.revolut.domain.entities.CurrencyAmount
import dev.temirlan.revolut.mvvm.list.CurrencyAmountAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainContract.View {

    private lateinit var viewModel: MainViewModel
    private lateinit var currencyAmountAdapter: CurrencyAmountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        currencyAmountAdapter =
            CurrencyAmountAdapter(
                mutableListOf(),
                viewModel::onCurrencySelected,
                viewModel::onCurrencyAmountEdited
            )

        viewModel.isLoadingLiveData.observe(this, Observer {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        })
        viewModel.currencyAmountsLiveData.observe(this, Observer(this::setCurrencyAmounts))
        viewModel.throwableLiveData.observe(this, Observer(this::showCommonError))

        srlRefresh.setOnRefreshListener {
            srlRefresh.isRefreshing = false
            viewModel.onRefreshed()
        }

        rvCurrencies.layoutManager = LinearLayoutManager(this)
        rvCurrencies.adapter = currencyAmountAdapter
        val currenciesAnimator = rvCurrencies.itemAnimator
        if(currenciesAnimator is SimpleItemAnimator) {
            currenciesAnimator.supportsChangeAnimations = false
        }
    }

    override fun showLoading() {
        pbProgress.visibility = View.VISIBLE
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                rvCurrencies?.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        rvCurrencies.startAnimation(animation)
    }

    override fun hideLoading() {
        pbProgress.visibility = View.GONE
        if (rvCurrencies.visibility != View.VISIBLE) {
            val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                }

                override fun onAnimationStart(animation: Animation?) {
                    rvCurrencies?.visibility = View.VISIBLE
                }
            })
            rvCurrencies.startAnimation(animation)
        }
    }

    override fun setCurrencyAmounts(currencyAmounts: List<CurrencyAmount>) {
        currencyAmountAdapter.update(currencyAmounts)
    }

    override fun showCommonError(throwable: Throwable) {
        Toast.makeText(this, throwable.localizedMessage, Toast.LENGTH_LONG).show()
    }
}
