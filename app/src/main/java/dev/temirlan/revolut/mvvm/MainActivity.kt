package dev.temirlan.revolut.mvvm

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.temirlan.revolut.R
import dev.temirlan.revolut.support.broadcast.NetworkConnectivityChangeReceiver
import dev.temirlan.revolut.domain.entities.CurrencyAmount
import dev.temirlan.revolut.mvvm.list.CurrencyAmountAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainContract.View {

    private lateinit var viewModel: MainViewModel
    private lateinit var currencyAmountAdapter: CurrencyAmountAdapter

    private var dialogNoConnection: AlertDialog? = null

    private val networkConnectivityReceiver = NetworkConnectivityChangeReceiver { isConnected ->
        if (isConnected) {
            viewModel.onRefreshed()
        } else {
            viewModel.onNetworkConnectionLost()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        currencyAmountAdapter = CurrencyAmountAdapter(
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
        viewModel.exceptionLiveData.observe(this, Observer(this::showCommonError))
        viewModel.isNetworkSettingsVisibleLiveData.observe(this, Observer {
            if (it) {
                showNetworkSettingsDialog()
            } else {
                hideNetworkSettingsDialog()
            }
        })

        srlRefresh.setOnRefreshListener {
            srlRefresh.isRefreshing = false
            viewModel.onRefreshed()
        }

        rvCurrencies.setHasFixedSize(true)
        rvCurrencies.setItemViewCacheSize(20)
        rvCurrencies.layoutManager = LinearLayoutManager(this)
        rvCurrencies.adapter = currencyAmountAdapter
        val currenciesAnimator = rvCurrencies.itemAnimator
        if (currenciesAnimator is SimpleItemAnimator) {
            currenciesAnimator.supportsChangeAnimations = false
        }
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(networkConnectivityReceiver, intentFilter)
    }

    override fun onStop() {
        unregisterReceiver(networkConnectivityReceiver)
        super.onStop()
    }

    override fun showLoading() {
        pbProgress.visibility = View.VISIBLE
        rvCurrencies.visibility = View.GONE
    }

    override fun hideLoading() {
        pbProgress.visibility = View.GONE
        if (rvCurrencies.visibility != View.VISIBLE) {
            rvCurrencies.visibility = View.VISIBLE
            rvCurrencies.startLayoutAnimation()
        }
    }

    override fun setCurrencyAmounts(currencyAmounts: List<CurrencyAmount>) {
        currencyAmountAdapter.update(currencyAmounts)
    }

    override fun showCommonError(exception: Exception?) {
        exception?.let {
            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun showNetworkSettingsDialog() {
        dialogNoConnection = MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.title_connection_lost))
            .setMessage(getString(R.string.description_connection_lost))
            .setPositiveButton(getString(R.string.common_yes)) { dialog, which ->
                val intent = Intent(Settings.ACTION_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                this.startActivity(intent)
            }
            .setNegativeButton(getString(R.string.common_no), null)
            .show()
    }

    override fun hideNetworkSettingsDialog() {
        dialogNoConnection?.dismiss()
    }
}
